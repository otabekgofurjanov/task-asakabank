import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UnluckyVassal {
    public void printReportForKing(List<String> pollResults) {

        //FOR LISTNI AYLANIB PERSONLARDAN ISMLARNI OLIB COMPARING YORDAMIDA SORTLAYDI
        Map<String, Person> personByName = new HashMap<>();

        for (String record : pollResults) {
            parseRecord(record, personByName);
        }

        List<Person> kingServants = personByName.values().stream()
                .filter(person -> person.getMaster() == null)
                .sorted(Comparator.comparing(Person::getName))
                .collect(Collectors.toList());

        if (kingServants.isEmpty() && !personByName.isEmpty()) throw new RuntimeException("List of servants is looped");

        Person king = new Person("король");
        king.setServants(kingServants);

        recursivePrint(king, 0, new HashSet<>());
    }

        //RO`YXAT BO`YLAB AYLANIB EKRANGA CHIQAZADI
    private void recursivePrint(Person person, int level, Set<Person> printedPersons) {
        if (printedPersons.contains(person)) throw new RuntimeException("List of servants is looped");
        printedPersons.add(person);

        for (int i = 0; i < level; i++) {
            System.out.print('\t');
        }
        System.out.println(person.getName());
        if (person.getServants() != null) {
            for (Person servant : person.getServants()) {
                recursivePrint(servant, level + 1, printedPersons);
            }
        }
    }

    //RO'YXATLAR (STRING) UCHUN SHABLON
    private static final Pattern RECORD = Pattern.compile("^(([а-яА-Я0-9 ]*): )?(([а-яА-Я0-9 ]*?), )*([а-яА-Я0-9 ]*)$");


    //PERSONNI KEY VA VALUELARI BO`YICHA MAP QILADI VA PATTERN BO`YICHA TEKSHIRADI
    private void parseRecord(String record, Map<String, Person> personByName) {
        if (record == null || record.isBlank()) return;
        if (!RECORD.matcher(record).find())
            throw new RuntimeException("Item \"" + record + "\" does not match pattern");

        String masterName = getManagers(record);
        List<String> servants = getServants(record);

        //AGAR HIZMATCHILAR YUQ BOLSA NEW CREATE QILINADI
        List<Person> servantList = servants.stream()
                .map(servant -> personByName.computeIfAbsent(servant, Person::new))
                .sorted(Comparator.comparing(Person::getName))
                .collect(Collectors.toList());

        //AGAR PERSONNI EGASI BO`LSA UNGA SET QILADI. RO'YHAT ICHIDA PERSONLAR VA ULARNING EGALARI AYLANADI
        if (masterName != null) {
            Person master = personByName.computeIfAbsent(masterName, Person::new);
            master.setServants(servantList);
            for (Person servant : servantList) {
                servant.setMaster(master);
            }
        }
    }

    //PERSONNING EGALARINI QAYTARADI
    private String getManagers(String record) {
        String result = null;
        int index = record.indexOf(":");
        if (index >= 0) {
            result = record.substring(0, index);
        }
        return result;
    }

    //HIZMATKORLARNI QAYTARADI
    private List<String> getServants(String record) {
        int index = record.indexOf(":");
        if (index >= 0) {
            record = record.substring(index + 2);
        }
        return List.of(record.split(", "));
    }


}
