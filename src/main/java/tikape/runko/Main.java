package tikape.runko;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.*;
import tikape.runko.domain.*;

public class Main {

    public static void main(String[] args) throws Exception {
        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }
        
        String jdbcOsoite = "jdbc:sqlite:yksisarvistentestitietokanta.db";

        if (System.getenv("DATABASE_URL") != null) {
            jdbcOsoite = System.getenv("DATABASE_URL");
        }

        Database database = new Database(jdbcOsoite);

        AlueDao alueDao = new AlueDao(database);
        AiheDao aiheDao = new AiheDao(database, alueDao);
        ViestiDao viestiDao = new ViestiDao(database, aiheDao);
        
        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            boolean virhesyote = false;
            
            if (req.queryParams().contains("virhesyote")) {
                virhesyote = true;
            }
            
            map.put("alueet", alueDao.findAll());
            map.put("virhesyote", virhesyote);

            return new ModelAndView(map, "Alueet");
        }, new ThymeleafTemplateEngine());
        
        get("/alue/:id", (req,res) -> {
            HashMap<String, Object> data = new HashMap<>();

            Integer sivuNro = 1;
            boolean virhesyote = false;
            
            if (req.queryParams().contains("sivu")) {
                sivuNro = Integer.parseInt(req.queryParams("sivu"));
            }
            
            if (req.queryParams().contains("virhesyote")) {
                virhesyote = true;
            }
            
            Alue alue = alueDao.findOne(Integer.parseInt(req.params(":id")));
            List<Aihe> aiheet = aiheDao.findAllInAluePerPage(alue, sivuNro);
            
            Integer aiheMaara = aiheDao.countAllInAlue(alue);
            Integer sivuMaara = (aiheMaara / 10) + 1;
            
            if (sivuNro > 1) {
                data.put("edellinen", sivuNro - 1);
            }
            
            if (sivuNro < sivuMaara) {
                data.put("seuraava", sivuNro + 1);
            }
            
            data.put("alue", alue);
            data.put("aiheet", aiheet);
            data.put("sivu", sivuNro);
            data.put("virhesyote", virhesyote);
            
            return new ModelAndView(data, "Aiheet");
        }, new ThymeleafTemplateEngine());
        
        get("/alue/:alueid/aihe/:aiheid", (req,res) -> {
            HashMap<String, Object> data = new HashMap();
            
            Integer sivuNro = 1;
            Integer raja = 4;
            boolean virhesyote = false;
            
            if (req.queryParams().contains("sivu")) {
                sivuNro = Integer.parseInt(req.queryParams("sivu"));
            } 
            
            if (req.queryParams().contains("raja")) {
                raja = Integer.parseInt(req.queryParams("raja"));
            }
            
            if (req.queryParams().contains("virhesyote")) {
                virhesyote = true;
            }
            
            Alue alue = alueDao.findOne(Integer.parseInt(req.params(":alueid")));
            Aihe aihe = aiheDao.findOne(Integer.parseInt(req.params(":aiheid")));
            List<Viesti> viestit = viestiDao.findAllInAihePerPage(aihe, sivuNro, raja);

            Integer sivuMaara = (aihe.getViestimaara() / raja) + 1;
            
            if (sivuNro > 1) {
                data.put("edellinen", sivuNro - 1);
            }
            
            if (sivuNro < sivuMaara) {
                data.put("seuraava", sivuNro + 1);
            }
            
            data.put("offset", raja * (sivuNro - 1) + 1);
            data.put("alue", alue);
            data.put("aihe", aihe);
            data.put("viestit", viestit);
            data.put("sivu", sivuNro);
            data.put("raja", raja);
            data.put("virhesyote", virhesyote);
            
            return new ModelAndView(data, "Viestit");
        }, new ThymeleafTemplateEngine());
        
        post("/addAlue", (req, res) -> {
            String nimi = req.queryParams("alueOtsikko");
            String kuvaus = req.queryParams("alueKuvaus");
            
            if (!nimi.isEmpty() && nimi.length() < 20) {
                Alue alue = new Alue(nimi, kuvaus);
                alueDao.create(alue);
                res.redirect("/alue/" + alue.getTunnus());
            } else {
                res.redirect("/?virhesyote=1");
            }
            
            return ""; 
        });
        
        post("/alue/:alueTunnus/addAihe", (req, res) -> {
            Alue alue = alueDao.findOne(Integer.parseInt(req.params(":alueTunnus")));
            String aloittaja = req.queryParams("aiheAloittaja");
            String otsikko = req.queryParams("aiheOtsikko");
            String sisalto = req.queryParams("aiheSisalto");
                    
            if (!aloittaja.isEmpty() && !otsikko.isEmpty() && !sisalto.isEmpty() &&
                 aloittaja.length() < 40 && otsikko.length() < 255 && sisalto.length() < 255) {
                Aihe aihe = new Aihe(alue ,aloittaja, sisalto, otsikko);
                aiheDao.create(aihe);
                res.redirect("/alue/" + req.params(":alueTunnus") + "/aihe/" + aihe.getTunnus());
            } else {
                res.redirect("/alue/" + req.params(":alueTunnus") + "?virhesyote=1");
            }
            
            return ""; 
        });
        
        post("/alue/:alueTunnus/aihe/:aiheTunnus/addViesti", (req, res) -> {
            Alue alue = alueDao.findOne(Integer.parseInt(req.params(":alueTunnus")));
            Aihe aihe = aiheDao.findOne(Integer.parseInt(req.params(":aiheTunnus")));
            
            String lahettaja = req.queryParams("viestiLahettaja");
            String teksti = req.queryParams("teksti");
            
            if (!teksti.isEmpty() && !lahettaja.isEmpty() &&
                teksti.length() < 255 && lahettaja.length() < 40) {
                Viesti viesti = new Viesti(aihe, teksti, lahettaja);
                viestiDao.create(viesti);
            } else {
                res.redirect("/alue/" + alue.getTunnus() + "/aihe/" + aihe.getTunnus() + "?virhesyote=1");
            }
            
            res.redirect("/alue/" + alue.getTunnus() + "/aihe/" + aihe.getTunnus());
            return "";
        });

//        Käytä alla oleva tekstikäyttöliittymä kyselyden testaamiseen
//        Cli cli = new Cli(database);
//        cli.start();
    }
}
