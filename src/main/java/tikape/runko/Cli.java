package tikape.runko;

import java.sql.SQLException;
import java.util.Scanner;

import tikape.runko.database.*;
import tikape.runko.domain.*;

public class Cli {
    private Database database;
    private AlueDao alueDao;
    private AiheDao aiheDao;
    private ViestiDao viestiDao;
    
    public Cli(Database database) {
        this.database = database;
        
        this.alueDao = new AlueDao(database);
        this.aiheDao = new AiheDao(database, alueDao);
        this.viestiDao = new ViestiDao(database, aiheDao);
    }
    
    public void start() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("CLI started");
        
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
        System.out.println("[11] find all Viesti in Aihe");
        System.out.println("[12] find all Viesti in Aihe per page");
        System.out.println("[99] quit");
        
        while (true) {
            System.out.print("Execute: ");
            String command = scanner.nextLine();
            
            if (command.equals("1")) {
                System.out.print("Alueen nimi: ");
                String nimi = scanner.nextLine();
                System.out.print("Alueen kuvaus");
                String kuvaus = scanner.nextLine();
                
                Alue alue = new Alue(nimi, kuvaus);
                alue = alueDao.create(alue);
                
                System.out.println("Alue created:");
                System.out.println(alue.toString());
            } else if (command.equals("2")) {
                System.out.println("Aluen tunnus: ");
                String tunnus = scanner.nextLine();
                System.out.println("Found alue: ");
                System.out.println(alueDao.findOne(Integer.parseInt(tunnus)).toString());
            } else if (command.equals("99")) {
                break;
            }
        }        
    }
}
