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
             // Käytetään testidataa
                // asetetaan portti jos heroku antaa PORT-ympäristömuuttujan
        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }
        
        String jdbcOsoite = "jdbc:sqlite:yksisarvistentestitietokanta.db";
        // jos heroku antaa käyttöömme tietokantaosoitteen, otetaan se käyttöön
        if (System.getenv("DATABASE_URL") != null) {
            jdbcOsoite = System.getenv("DATABASE_URL");
        }

        Database database = new Database(jdbcOsoite);
        database.init();

        AlueDao alueDao = new AlueDao(database);
        AiheDao aiheDao = new AiheDao(database, alueDao);
        ViestiDao viestiDao = new ViestiDao(database, aiheDao);
        
        get("/", (req, res) -> {
            HashMap map = new HashMap<>();

            map.put("alueet", alueDao.findAll());

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        
        get("/alue/:id", (req,res) -> {
            HashMap<String, Object> data = new HashMap<>();

            Integer sivuNro = 1;
            
            if (req.queryParams().contains("sivu")) {
                sivuNro = Integer.parseInt(req.queryParams("sivu"));
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
            
            return new ModelAndView(data, "area");
        }, new ThymeleafTemplateEngine());
        
        get("/alue/:alueid/aihe/:aiheid", (req,res) -> {
            HashMap<String, Object> data = new HashMap();
            
            Integer sivuNro = 1;
            Integer raja = 5;
            
            if (req.queryParams().contains("sivu")) {
                sivuNro = Integer.parseInt(req.queryParams("sivu"));
            } 
            
            if (req.queryParams().contains("raja")) {
                raja = Integer.parseInt(req.queryParams("raja"));
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
            
            data.put("indeksi", raja * (sivuNro - 1) + 1);
            data.put("alue", alue);
            data.put("aihe", aihe);
            data.put("viestit", viestit);
            data.put("sivu", sivuNro);
            
            return new ModelAndView(data, "thread");
        }, new ThymeleafTemplateEngine());
        
        post("/addAlue", (req, res) -> {
            String alueOtsikko = req.queryParams("alueOtsikko");
            String alueKuvaus = req.queryParams("alueKuvaus");
            Alue alue = new Alue(alueOtsikko, alueKuvaus);
            
            alue = alueDao.create(alue);

            res.redirect("/alue/" + alue.getTunnus());
            return ""; 
        });
        
        post("/alue/:alueTunnus/addAihe", (req, res) -> {
            Alue alue = alueDao.findOne(Integer.parseInt(req.params(":alueTunnus")));
            String aiheAloittaja = req.queryParams("aiheAloittaja");
            String aiheOtsikko = req.queryParams("aiheOtsikko");
            String aiheSisalto = req.queryParams("aiheSisalto");
            //String viestiTeksti = req.queryParams("viesti");
            
            Aihe aihe = new Aihe(alue ,aiheAloittaja, aiheSisalto, aiheOtsikko);
            aihe = aiheDao.create(aihe);
            
            //Viesti viesti = new Viesti(aihe, viestiTeksti, aiheAloittaja, aihe.getLuotu());
            //viestiDao.create(viesti);            
            
            res.redirect("/alue/" + req.params(":alueTunnus") + "/aihe/" + aihe.getTunnus());
            return ""; 
        });
        
        post("/alue/:alueTunnus/aihe/:aiheTunnus/addViesti", (req, res) -> {
            Alue alue = alueDao.findOne(Integer.parseInt(req.params(":alueTunnus")));
            Aihe aihe = aiheDao.findOne(Integer.parseInt(req.params(":aiheTunnus")));
            String viestiTeksti = req.queryParams("teksti");
            String viestiLahettaja = req.queryParams("viestiLahettaja");
            
            Viesti viesti = new Viesti(aihe, viestiTeksti, viestiLahettaja);
            viestiDao.create(viesti);
            
            res.redirect("/alue/" + alue.getTunnus() + "/aihe/" + aihe.getTunnus());
            return "";
        });

//        Käytä alla oleva tekstikäyttöliittymä kyselyden testaamiseen
//        Cli cli = new Cli(database);
//        cli.start();
    }
}
