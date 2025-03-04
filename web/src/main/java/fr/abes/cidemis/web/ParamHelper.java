package fr.abes.cidemis.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.cidemis.components.Fichier;
import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.exception.DaoException;
import fr.abes.cidemis.model.cidemis.Connexion;
import fr.abes.cidemis.model.cidemis.RegistryUser;
import fr.abes.cidemis.service.CidemisManageService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Charsets;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Getter
@Setter
public class ParamHelper {
    @Autowired
    private CidemisManageService service;

    private HttpServletRequest request = null;
    private HashMap<String, List<String>> multipartParams = null;
    private HashMap<String, List<Fichier>> multipartFiles = null;
    private boolean isMultipart = false;


    // Récupère les paramètres dans le cas d'un formulaire MultiPart
    public ParamHelper() {

    }

    public ParamHelper(HttpServletRequest request) {
        setRequest(request);
    }

    public void setRequest(HttpServletRequest request) {
        this.isMultipart = false;
        this.request = request;
        String enctype = request.getHeader("Content-type");

        if (enctype != null && enctype.startsWith("multipart/form-data")) {
            this.isMultipart = true;
            getMultipartForm(request);
        }
    }

    public RegistryUser readUserWsProfile(URL url, String userKey, String password) {
        // Je lance la connexion
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String input = "{\"userKey\":\"" + userKey + "\",\"password\":\"" + password + "\"}";

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                //Gestion de l'erreur
                log.error("Login et / ou mot de passe erroné");
                return null;
            }
            InputStream reponse = conn.getInputStream();
            StringBuffer result = new StringBuffer();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(reponse, Charsets.UTF_8))) {
                for (String line; (line = reader.readLine()) != null; ) {
                    result.append(line);
                }
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode noeud = mapper.readTree(result.toString());
            RegistryUser user = new RegistryUser();
            user.setUserNum(noeud.get("userNum").asInt());
            user.setUserKey(noeud.get("userKey").asText());
            user.setUserGroup(noeud.get("userGroup").asText());
            user.setLibrary(noeud.get("library").asText());
            user.setShortName(noeud.get("shortName").asText());
            user.setLoginAllowed((noeud.get("loginAllowed").asText().equals("Y") ? true : false));
            user.setIln(noeud.get("iln").asText());

            return user;
        } catch (IOException e) {
            log.error("Appel au WebService d'authentification au sudoc échoué");
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * Connecte l'utilisateur à l'application / Etablie la connexion au CBS
     * <p>
     * On tente de se connecter
     * Récupère un objet Connexion contenant l'user et le client cbs
     * renvoie null si problème à la connexion
     *
     * @param session
     * @param user    : entité résultat de l'appel au ws d'authentification
     * @return
     * @throws IOException
     */
    public Boolean login(HttpSession session, RegistryUser user) throws DaoException {
        Connexion connexion = service.getUsers().login(user);

        if (connexion == null) {
            return false;
        } else {
            session.setAttribute("connexion", connexion);
            session.setAttribute("constant", new Constant());
            return true;
        }
    }

    public String[] getParameters(String paramName) {
        String[] parameters;

        if (this.isMultipart) {
            List<String> list = this.multipartParams.get(paramName);

            if (list != null) {
                parameters = new String[list.size()];
                list.toArray(parameters);
            } else {
                parameters = new String[]{};
            }

            // Si on n'a pas récupéré le paramètre dans les parts
            // On teste si il n'a pas été passé en get..
            if (parameters.length == 0 && this.request.getQueryString() != null)
                parameters = this.request.getParameterValues(paramName);
        } else {
            parameters = request.getParameterValues(paramName);
        }

        return parameters;
    }

    public String getParameter(String paramName) {
        String parameter;

        if (this.isMultipart) {
            List<String> list = this.multipartParams.get(paramName);

            if (list != null)
                parameter = trim(list.get(0));
            else
                parameter = "";

            // Si on n'a pas récupéré le paramètre dans les parts
            // On teste si il n'a pas été passé en get..
            if (parameter.isEmpty() && this.request.getQueryString() != null)
                parameter = trim(this.request.getParameter(paramName));
        } else {
            parameter = trim(request.getParameter(paramName));
        }

        return parameter;
    }

    public List<Fichier> getFile(String fileName) {
        if (this.isMultipart)
            return this.multipartFiles.containsKey(fileName) ? this.multipartFiles.get(fileName) : new ArrayList();
        else
            return new ArrayList();
    }

    private String getValue(Part part) throws IOException {
        InputStreamReader is = new InputStreamReader(part.getInputStream(), this.request.getCharacterEncoding());
        BufferedReader reader = new BufferedReader(is);
        StringBuilder value = new StringBuilder();
        char[] buffer = new char[10240];

        for (int length; (length = reader.read(buffer)) > 0; )
            value.append(buffer, 0, length);

        is.close();
        return value.toString();
    }

    // Récupère les champs et fichiers d'un formulaire multipart
    private void getMultipartForm(HttpServletRequest request) {
        try {
            this.multipartParams = new HashMap();
            this.multipartFiles = new HashMap();

            for (Part part : request.getParts()) {
                String filename = extractFileName(part);

                // Si ce n'est pas un fichier (input text...)
                if (filename.isBlank()) {
                    List<String> values = this.multipartParams.get(part.getName());
                    values = values == null ? new ArrayList() : values;

                    values.add(getValue(part));
                    multipartParams.put(part.getName(), values);
                }
                // Si c'est un fichier
                else {
                    List<Fichier> fichiers = this.multipartFiles.get(part.getName());
                    fichiers = fichiers == null ? new ArrayList() : fichiers;

                    File temp = File.createTempFile("justificatif", filename);
                    temp.deleteOnExit();
                    part.write(temp.getAbsolutePath());
                    log.info(temp.getAbsolutePath());

                    // On rempli la structure fichier.
                    Fichier fichier = new Fichier();
                    fichier.setFile(temp);
                    fichier.setFilename(filename);

                    fichiers.add(fichier);
                    this.multipartFiles.put(part.getName(), fichiers);
                }


            }
        } catch (IOException ex) {
            log.info("Pas de fichier ajouté à la demande");
        } catch (IllegalStateException ex) {
            log.error(null, ex);
        } catch (ServletException ex) {
            log.error(null, ex);
        }
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf('=') + 2, s.length() - 1);
            }
        }
        return "";
    }

    private String trim(String string) {
        return (string != null) ? string.trim() : "";
    }


}
