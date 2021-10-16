package dizionarioPerCitta;

import java.util.HashMap;

public class Province {
    public static final String [] prova_provincePugliesi = {"BT", "BA"};

    public static HashMap<String, String []> map = null;

    static {
        map = new HashMap<String, String[]>();
        map.put("Abruzzo", new String[]{"AQ", "CH", "PE", "TE"});
        map.put("Basilicata", new String [] {"MT", "PZ"});
        map.put("Calabria", new String [] {"CS", "CZ", "KR", "RC", "VV"});
        map.put("Campania", new String [] {"AV", "BN", "CE", "NA", "SA"});
        map.put("Emilia-Romagna", new String [] {"BO", "FC", "FE", "MO", "PC", "PR", "RA", "RE", "RN"});
        map.put("Friuli-Venezia Giulia", new String [] {"GO", "PN", "TS", "UD"});
        map.put("Lazio", new String [] {"FR", "LT", "RI", "RM", "VT"});
        map.put("Liguria", new String [] {"GE", "IM", "SP", "SV"});
        map.put("Lombardia", new String [] {"BG", "BS", "CO", "CR", "LC", "LO", "MB", "MI", "MN", "PV", "SO", "VA"});
        map.put("Marche", new String [] {"AN", "AP", "FM", "MC", "PU"});
        map.put("Molise", new String [] {"CB", "IS"});
        map.put("Piemonte", new String [] {"AL", "AT", "BI", "CN", "NO", "TO", "VB", "VC"});
        map.put("Puglia", new String [] {"BA", "BR", "BT", "FG", "LE", "TA"});
        map.put("Sardegna", new String [] {"CA", "NU", "OR", "SS", "SU"});
        map.put("Sicilia", new String [] {"AG", "CL", "CT", "EN", "ME", "PA", "RG", "SR", "TP"});
        map.put("Toscana", new String [] {"AR", "FI", "GR", "LI", "LU", "MS", "PI", "PO", "PT", "SI"});
        map.put("Trentino-Alto Adige", new String [] {"BZ", "TN"});
        map.put("Umbria", new String [] {"PG", "TR"});
        map.put("Valle d'Aosta", new String [] {"AO"});
        map.put("Veneto", new String [] {"BL", "PD", "RO", "TV", "VE", "VI", "VR"});
    }

}
