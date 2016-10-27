package tikape.runko;

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
        Database database = new Database("jdbc:sqlite:yksisarvistentestitietokanta.db");
        database.init();

        AlueDao alueDao = new AlueDao(database);
        AiheDao aiheDao = new AiheDao(database, alueDao);
        ViestiDao viestiDao = new ViestiDao(database, aiheDao);
        
        get("/", (req, res) -> {
            HashMap map = new HashMap<>();

            map.put("alueet", alueDao.findAll());

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        
        get("/alue/:id", (req, res) -> { 
            res.redirect("/alue/" + Integer.parseInt(req.params(":id")) + "/sivu/1");
            return "";
        });
        
        get("/alue/:id/sivu/:nro", (req,res) -> {
            HashMap<String, Object> data = new HashMap<>();
            
            Integer nro = Integer.parseInt(req.params(":nro"));
            Alue alue = alueDao.findOne(Integer.parseInt(req.params(":id")));
            List<Aihe> aiheet = aiheDao.findAllInAluePerPage(alue, nro);
            
            data.put("alue", alue);
            data.put("aiheet", aiheet);
            data.put("sivu", nro);
            
            return new ModelAndView(data, "area");
        }, new ThymeleafTemplateEngine());
        
        get("/alue/:aid/aihe/:id", (req, res) -> {
            res.redirect("/alue/" + req.params(":aid") + "/aihe/" + req.params(":id") + "/sivu/1");
            return "";
        });
        
        get("/alue/:aid/aihe/:id/sivu/:nro", (req,res) -> {
            HashMap<String, Object> data = new HashMap();
            
            Integer nro = Integer.parseInt(req.params(":nro"));
            Alue alue = alueDao.findOne(Integer.parseInt(req.params(":aid")));
            Aihe aihe = aiheDao.findOne(Integer.parseInt(req.params(":id")));
            List<Viesti> viestit = viestiDao.findAllInAihePerPage(aihe, nro, 10);
            
            data.put("alue", alue);
            data.put("aihe", aihe);
            data.put("viestit", viestit);
            
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
            String viestiTeksti = req.queryParams("viesti");
            
            Aihe aihe = new Aihe(alue ,aiheAloittaja, aiheSisalto, aiheOtsikko);
            aihe = aiheDao.create(aihe);
            
            Viesti viesti = new Viesti(aihe, viestiTeksti, aiheAloittaja, aihe.getLuotu());
            viesti = viestiDao.create(viesti);            
            
            res.redirect("/alue/" + req.params(":alueTunnus") + "/aihe/" + aihe.getTunnus());
            return ""; 
        });
        
        post("/alue/:alueTunnus/aihe/:aiheTunnus/addViesti", (req, res) -> {
            Alue alue = alueDao.findOne(Integer.parseInt(req.params(":alueTunnus")));
            Aihe aihe = aiheDao.findOne(Integer.parseInt(req.params(":aiheTunnus")));
            String viestiTeksti = req.queryParams("teksti");
            String viestiLahettaja = req.queryParams("viestiLahettaja");
            
            Viesti viesti = new Viesti(aihe, viestiTeksti, viestiLahettaja);
            viesti = viestiDao.create(viesti);
            
            res.redirect("/alue/" + alue.getTunnus() + "/aihe/" + aihe.getTunnus());
            return "";
        });
    }
}
