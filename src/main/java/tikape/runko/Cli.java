package tikape.runko;

import java.sql.SQLException;
import java.util.Scanner;
import java.util.List;

import tikape.runko.database.*;
import tikape.runko.domain.*;

public class Cli {
    private Database database;
    private AlueDao alueDao;
    private AiheDao aiheDao;
    private ViestiDao viestiDao;
    private Scanner scanner;
    
    public Cli(Database database) {
        this.database = database;
        
        this.alueDao = new AlueDao(database);
        this.aiheDao = new AiheDao(database, alueDao);
        this.viestiDao = new ViestiDao(database, aiheDao);
        this.scanner = new Scanner(System.in);
    }
    
    public void start() throws SQLException {
        
        System.out.println("Tekstikäyttöliittymä käynnissä");
        System.out.println("Komennot:");
        
        Commands();
        
        while (true) {
            System.out.print("Anna komento: ");
            String command = scanner.nextLine();
            
            if (command.equals("1")) {
                
                createAlue();
                
            } else if (command.equals("2")) {
                
                findOneAlue();
                
            } else if (command.equals("3")) {

                findAllAlue();
                
            } else if (command.equals("4")) {
                
                createAihe();
                
            } else if (command.equals("5")) {
                
                findOneAihe();
                
            } else if (command.equals("6")) {

                findAllAihe();
            
            } else if (command.equals("7")) {
                
                findAllAiheInAlue();
                
            } else if (command.equals("8")) { 
                
                findAllAiheInAluePerPage();
                
            } else if (command.equals("9")) {
                
                createViesti();
                
            } else if (command.equals("10")) {
                
                findOneViesti();
                
            } else if (command.equals("11")) {
                
                findAllViesti();
                
            } else if (command.equals("12")) {
            
                findAllViestiInAihe();
            
            } else if (command.equals("13")) {
            
                findAllViestiInAihePerPage();
            
            } else if (command.equals("99")) {
                
                break;

            } else if (command.equals("komennot")) {
                
                Commands();
                
            } else {
                
                System.out.println("Tuntematon komento. Komento \"komennot\" listaa kaikki komennot.");
                
            }
        }
    }
    
    private void Commands() {
        System.out.println("[1] create Alue");
        System.out.println("[2] find one Alue");
        System.out.println("[3] find all Alue");
        System.out.println("[4] create Aihe");
        System.out.println("[5] find one Aihe");
        System.out.println("[6] find all Aihe");
        System.out.println("[7] find all Aihe in Alue");
        System.out.println("[8] find all Aihe in Alue per page");
        System.out.println("[9] create Viesti");
        System.out.println("[10] find one Viesti");
        System.out.println("[11] find all Viesti");
        System.out.println("[12] find all Viesti in Aihe");
        System.out.println("[13] find all Viesti in Aihe per page");
        System.out.println("[99] quit");
        System.out.println("[komennot] näytä tämä lista");
    }
    
    private void createAlue() throws SQLException {
        System.out.print("Aluen nimi: ");
        String nimi = scanner.nextLine();
        System.out.print("Aluen kuvaus: ");
        String kuvaus = scanner.nextLine();
                
        Alue alue = new Alue(nimi, kuvaus);
        alue = alueDao.create(alue);
                
        System.out.println("Luotu alue:");
        System.out.println(alue);        
    }
    
    private void findOneAlue() throws SQLException {
        System.out.print("Aluen tunnus: ");
        String tunnus = scanner.nextLine();

        System.out.println("Löydetty alue: ");
        System.out.println(alueDao.findOne(Integer.parseInt(tunnus)));
    }
    
    private void findAllAlue() throws SQLException {               
        List<Alue> alueet = alueDao.findAll();

        System.out.println("Löydetty alueet: ");

        for (Alue alue : alueet) {
            System.out.println(alue);
        }
    }
    
    private void createAihe() throws SQLException {
        System.out.print("Mihin alueeseen (aluetunnus): ");
        String aluetunnus = scanner.nextLine();
        Alue alue = alueDao.findOne(Integer.parseInt(aluetunnus));
        System.out.print("Aiheen aloittaja: ");
        String aloittaja = scanner.nextLine();
        System.out.print("Aihen otsikko: ");
        String otsikko = scanner.nextLine();
        System.out.print("Aiheen sisältö: ");
        String sisalto = scanner.nextLine();

        Aihe aihe = new Aihe(alue, aloittaja, sisalto, otsikko);
        aihe = aiheDao.create(aihe);

        System.out.println("Luotu aihe: ");
        System.out.println(aihe);
    }
    
    private void findOneAihe() throws SQLException {
        System.out.print("Aiheen tunnus: ");
        String tunnus = scanner.nextLine();

        System.out.println("Löydetty aihe: ");
        System.out.println(aiheDao.findOne(Integer.parseInt(tunnus)));
    }
    
    private void findAllAihe() throws SQLException {
        List<Aihe> aiheet = aiheDao.findAll();

        System.out.println("Löydetty aiheet: ");

        for (Aihe aihe : aiheet) {
            System.out.println(aihe);
        }
    }
    
    private void findAllAiheInAlue() throws SQLException {
        System.out.print("Alueen tunnus: ");
        String tunnus = scanner.nextLine();

        Alue alue = alueDao.findOne(Integer.parseInt(tunnus));

        List<Aihe> aiheet = aiheDao.findAllInAlue(alue);

        System.out.println("Löydetty aiheet: ");

        for (Aihe aihe : aiheet) {
            System.out.println(aihe);
        }        
    }
    
    private void findAllAiheInAluePerPage() throws SQLException {
        System.out.print("Aiheen tunnus: ");
        String tunnus = scanner.nextLine();
        System.out.print("Sivu: ");
        String sivu = scanner.nextLine();

        Alue alue = alueDao.findOne(Integer.parseInt(tunnus));

        List<Aihe> aiheet = aiheDao.findAllInAluePerPage(alue, Integer.parseInt(sivu));

        System.out.println("Löydetty aiheet: ");

        for (Aihe aihe : aiheet) {
            System.out.println(aihe);
        }        
    }
    
    private void createViesti() throws SQLException {
        System.out.print("Aiheen tunnus: ");
        String tunnus = scanner.nextLine();
        Aihe aihe = aiheDao.findOne(Integer.parseInt(tunnus));
        System.out.print("Lähettäjä: ");
        String lahettaja = scanner.nextLine();
        System.out.print("Viesti: ");
        String teksti = scanner.nextLine();

        Viesti viesti = new Viesti(aihe, teksti, lahettaja);
        viesti = viestiDao.create(viesti);

        System.out.println("Luotu viesti: ");
        System.out.println(viesti);
    }
    
    private void findOneViesti() throws SQLException {
        System.out.print("Viestin tunnus: ");
        String tunnus = scanner.nextLine();

        Viesti viesti = viestiDao.findOne(Integer.parseInt(tunnus));

        System.out.println("Löydetty viesti: ");
        System.out.println(viesti);
    }
    
    private void findAllViesti() throws SQLException {
        List<Viesti> viestit = viestiDao.findAll();

        System.out.println("Löydetty viestit: ");

        for(Viesti viesti : viestit) {
            System.out.println(viesti);
        }
    }
    
    private void findAllViestiInAihe() throws SQLException {
        System.out.print("Aiheen tunnus: ");
        String tunnus = scanner.nextLine();
        Aihe aihe = aiheDao.findOne(Integer.parseInt(tunnus));

        List<Viesti> viestit = viestiDao.findAllInAihe(aihe);

        System.out.println("Löydetty viestit: ");

        for (Viesti viesti : viestit) {
            System.out.println(viesti);
        }
    }
    
    private void findAllViestiInAihePerPage() throws SQLException {
        System.out.print("Aiheen tunnus: ");
        String tunnus = scanner.nextLine();
        Aihe aihe = aiheDao.findOne(Integer.parseInt(tunnus));
        System.out.print("Sivunumero: ");
        String nro = scanner.nextLine();
        System.out.print("Viestimäärä per sivu: ");
        String viestimaara = scanner.nextLine();

        List<Viesti> viestit = viestiDao.findAllInAihePerPage(
                aihe, Integer.parseInt(tunnus), Integer.parseInt(viestimaara));
    }
}
