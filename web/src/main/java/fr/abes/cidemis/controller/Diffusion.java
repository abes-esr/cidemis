/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.abes.cidemis.controller;

import fr.abes.cidemis.model.cidemis.PiecesJustificatives;
import fr.abes.cidemis.service.CidemisManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.net.ssl.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;

/**
 * Servlet de diffusion
 */
@Slf4j
@Controller
public class Diffusion extends HttpServlet {
	@Autowired
	private CidemisManageService service;

	@Value("${path.justificatifs}")
	private String path;

	public void init(ServletConfig config) throws ServletException {
		try {
			// Permet d'accepter les certificats SSL des urls https à tester
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] certs, String authType) {
					// Pour tout accepter
				}

				@Override
				public void checkServerTrusted(X509Certificate[] certs, String authType) {
					// Pour tout accepter
				}
			} };

			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = (String hostname, SSLSession session) -> true;

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (Exception ex) {
			log.error( "Diffusion SSL Context : {0}", ex);
		}
	}

	@RequestMapping(value = "/diffusion", method = RequestMethod.GET)
	protected void diffusion(HttpServletRequest request, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8");
		if (request.getParameter("id") != null) {
			Integer pjid = Integer.parseInt(request.getParameter("id"));
			PiecesJustificatives pj = this.service.getPiecesJustificatives().findPiecesJustificatives(pjid);
			String fichier = pj.getPathFichier(path);

			try {
				returnFile(request, response, fichier, pj.getPublicname());
			} catch (Exception e) {
				Diffusion.log.error( "Erreur envoi du fichier : {0}", e);
			}

		}
	}

	/**
	 * Renvoi le fichier demandé
	 */
	private void returnFile(HttpServletRequest request, HttpServletResponse response, String file, String nomfichier) {
		log.info("Fichier : {0}", file);
		log.info("  User-agent:{0}", request.getHeader("User-Agent"));

		File f = new File(file);

		if (f.exists() && !f.isDirectory()) {
			// Essai de trouver le bon type du document
			response.reset();
			response.addHeader("Content-Disposition", "attachment; filename=" + nomfichier);

			// Renvoi le fichier tel quel
			try (ServletOutputStream stream = response.getOutputStream()) {
				response.setContentLength((int) f.length());
				getStreamContent(stream, f);
			} catch (IOException e) {
				log.error( e.getMessage(), e);
			}
		} else {
			response.setStatus(404);
			try {
				PrintWriter out = response.getWriter();
				out.println("<b>Ce fichier n'a pas pu être trouvé</b>");
			} catch (IOException e) {
				log.error( e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Alimente le flux à partir d'un fichier source
	 * @param stream flux à alimenter
	 * @param f fichier source
	 */
	private void getStreamContent(ServletOutputStream stream, File f) {
		try (FileInputStream fs = new FileInputStream(f.getAbsolutePath());) {
			byte[] buf = new byte[4 * 1024]; // 4K buffer
			int bytesRead;

			while ((bytesRead = fs.read(buf)) != -1)
				stream.write(buf, 0, bytesRead);
		} catch (IOException e) {
			log.error( e.getMessage(), e);
		}
	}

	/**
	 * Retourne true si l'url répond, false sinon
	 */
	public static boolean exists(String urlName) {
		try {
			if (urlName.trim().toLowerCase().startsWith("ftp://")) {
				URL url = new URL(urlName);
				URLConnection yc = url.openConnection();
				yc.connect();
				return true;
			} else {
				HttpURLConnection.setFollowRedirects(true);
				// note : you may also need
				// HttpURLConnection.setInstanceFollowRedirects(false)
				HttpURLConnection con = (HttpURLConnection) new URL(urlName).openConnection();
				con.setRequestMethod("HEAD");
				boolean reponse = false;
				if ((con.getResponseCode() == HttpURLConnection.HTTP_OK)
						|| (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP)
						|| (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM))
					reponse = true;
				return reponse;
			}
		} catch (Exception e) {
			log.error( e.getMessage(), e);
			return false;
		}
	}

	/**
	 * Returns a short description of the servlet.
	 * 
	 * @return a String containing servlet description
	 */
	public String getServletInfo() {
		return "Servlet de diffusion des PJ";
	}
}
