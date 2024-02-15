package fr.abes.cidemis.dao;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.model.CodePays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CodePaysDao {
	@Autowired
    public Map<String, CodePays> getAllCodePays(@Value("${code.pays.url}") String urlCodePays) {
        Map<String, CodePays> codePaysListe = new HashMap ();
        
        try {
            String line;
            URL url = new URL(urlCodePays);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), Constant.ENCODE));
            CodePays codePays;
            while ((line = rd.readLine()) != null) {
                String[] lignetab = line.split(Constant.CODE_PAYS_SEPARATEUR);
                if (!"PAYS".equals(lignetab[0])){
                    lignetab[3] = lignetab[3].replaceAll("_", "0");
                    lignetab[3] = (lignetab[3].length()==1)?"0" + lignetab[3]:lignetab[3];
                    codePays = new CodePays(lignetab[0], lignetab[1], lignetab[2], lignetab[3], lignetab[4]);
                    codePaysListe.put(codePays.getIso31661a2(), codePays);
                }
            }
            rd.close();
        }
        catch (IOException e) {
            log.error("Oups, erreur dans la récupération des codes pays", e);
        }

        return codePaysListe;
    }
    
}
