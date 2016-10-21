package tikape.runko;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
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
        
        get("/:id", (req, res) -> {            
            HashMap<String, Object> data = new HashMap<>();

            Alue alue = alueDao.findOne(Integer.parseInt(req.params(":id")));
            
            data.put("alue", alue);
            data.put("aiheet", aiheDao.findAllInAlue(alue));
            
            return new ModelAndView(data, "area");
        }, new ThymeleafTemplateEngine());
        
        get("/:aid/:id", (req, res) -> {
            HashMap<String, Object> data = new HashMap();

            Alue alue = alueDao.findOne(Integer.parseInt(req.params(":aid")));
            Aihe aihe = aiheDao.findOne(Integer.parseInt(req.params(":id")));
            List<Viesti> viestit = viestiDao.findAllInAihe(aihe);
            
            data.put("alue", alue);
            data.put("aihe", aihe);
            data.put("viestit", viestit);

            return new ModelAndView(data, "thread");
        }, new ThymeleafTemplateEngine());
    }
}
