package table;

import b_plus_tree.BPlusTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Table {
    public boolean validateColumn(String name, String type) {

        if (!getColumn().containsKey(name)) {
            getColumn().put(name, type);
            return true;
        } else
            return false;
    }

    private boolean uniq;
    private String name;
    private String firstUnicodeType = "";
    private String secondUnicodeType = "";
    private boolean selfCounter;
    private int counter;
    private Validation validation;
    private final double fixedCode = 1;
    private Map<String, String> column;
    ArrayList<Map<String, String>> records;
    private BPlusTree tree;

    public Table(String name) {
        this.name = name;
        this.column = new HashMap<>();
        this.records = new ArrayList<>();
        this.tree = new BPlusTree();
    }

    public void newValidation() {
        this.validation = new Validation(getFirstUnicodeType(), getSecondUnicodeType(), isSelfCounter(), isUniq());
    }

    public Validation getValidation() {
        return validation;
    }

    public void setValidation(Validation validation) {
        this.validation = validation;
    }

    public double getFixedCode() {
        return fixedCode;
    }

    public BPlusTree getTree() {
        return tree;
    }

    public void setTree(BPlusTree tree) {
        this.tree = tree;
    }

    public String getFirstUnicodeType() {
        return firstUnicodeType;
    }

    public void setFirstUnicodeType(String firstUnicodeType) {
        this.firstUnicodeType = firstUnicodeType;
    }

    public String getSecondUnicodeType() {
        return secondUnicodeType;
    }

    public void setSecondUnicodeType(String secondUnicodeType) {
        this.secondUnicodeType = secondUnicodeType;
    }

    public boolean isSelfCounter() {
        return selfCounter;
    }

    public void setSelfCounter(boolean selfCounter) {
        this.selfCounter = selfCounter;
    }

    public boolean isUniq() {
        return uniq;
    }

    public void setUniq(boolean uniq) {
        this.uniq = uniq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public Map<String, String> getColumn() {
        return column;
    }

    public void setColumn(Map<String, String> column) {
        this.column = column;
    }


    //================================edit functions==============================
    public boolean editRecord(double firstCode, double secondCode, String column, String data) {
        if (!getColumn().containsKey(column))
            return false;
        else if (getValidation().validateEditation(firstCode, secondCode, column, getColumn().get(column), data, getTree())) {
            getTree().search(firstCode).get(secondCode).put(column, data);
            if (column.equals(getFirstUnicodeType())) {
                getTree().insert(getValidation().getFirstCode(), getTree().search(firstCode));
                getTree().delete(firstCode);
            }
            if (column.equals(getSecondUnicodeType())) {
                getTree().search(firstCode).put(getValidation().getSecondCode(), getTree().search(firstCode).get(secondCode));
                getTree().search(firstCode).remove(secondCode);
            }
            setRecords();
            return true;
        }
        return false;
    }


    //============================remove functions============================
    public boolean removeColumn(String columnName) {
        if (getValidation().removeColumn(columnName) && !columnName.equals(getFirstUnicodeType()) && !columnName.equals(getSecondUnicodeType())) {
            getColumn().remove(columnName);
            for (Map<Double, Map<String, String>> map : getTree().getCollection())
                for (Map<String, String> record : map.values())
                    record.remove(columnName);
            setRecords();
            return true;
        }
        return false;
    }

    public boolean removeRecord(double firstUnicode, double secondUnicode) {
        if (getValidation().removeRecord(firstUnicode, secondUnicode, getTree())) {
            getTree().search(firstUnicode).remove(secondUnicode);
            setRecords();
            return true;
        }
        return false;
    }

    public boolean removeRecords(double firstUnicode) {
        if (getValidation().removeRecord(firstUnicode, getTree())) {
            getTree().delete(firstUnicode);
            setRecords();
            return true;
        }
        return false;
    }
    //=========================search functions========================

    public StringBuilder searchUniqRecords(double firstUnicode, double secondUnicode) {
        StringBuilder result = new StringBuilder();
        try {
            getTree().search(firstUnicode).get(secondUnicode);
        } catch (Exception e) {
            return result.append("this records doesnt exist!");
        }

        for (String column : getColumn().keySet()) {
            result.append(column + " : " + getTree().search(firstUnicode).get(secondUnicode).get(column) + "\n");
        }
        return result;
    }

    public StringBuilder searchRecords(double firstUnicode) {
        StringBuilder result = new StringBuilder();
        try {
            getTree().search(firstUnicode);
        } catch (Exception e) {
            return result.append("this records doesnt exist!");
        }
        int counter3 = 0;
        for (Map<String, String> map : getTree().search(firstUnicode).values()) {
            result.append(getTree().search(firstUnicode).keySet().toArray()[counter3] + "\n");
            counter3++;
            for (String column : map.keySet()) {
                result.append(column + " : " + map.get(column) + "\n");
            }

        }
        return result;
    }

    public StringBuilder rangeSearch(double from, double to) {
        StringBuilder result = new StringBuilder();
        for (Map<Double, Map<String, String>> mainMap : getTree().getCollectionInRange(from, to))
            for (Map<String, String> map : mainMap.values()) {
                for (String column : map.keySet())
                    result.append(column + " : " + map.get(column) + "\n");
                result.append("******\n");
            }
        return result;
    }

    public StringBuilder spcialRecords(String columnname, String value) {
        StringBuilder result = new StringBuilder();
        for (Map<String, String> map : getRecords()) {
            if (map.get(columnname).equals(value)) {
                for (String column : map.keySet())
                    result.append(column + " : " + map.get(column) + "\n");
            }
        }
        return result;
    }

    public StringBuilder printAllRecords() {
        StringBuilder result = new StringBuilder();
        for (Map<String, String> map : getRecords()) {
            for (String column : map.keySet())
                result.append(column + " : " + map.get(column) + "\n");
        }
        return result;
    }

    //=================add functions==================
    public boolean addNewColumn(String column, String dataType) {
        if (getValidation().addNewColumn(column, dataType, getColumn())) {
            getColumn().put(column, dataType);
            for (Map<Double, Map<String, String>> map : getTree().getCollection())
                for (Map<String, String> record : map.values())
                    record.put(column, null);
            setRecords();
            return true;
        }
        return false;
    }

    public boolean addRecords(String record) {
        String[] details = record.split(" ");
        getValidation().setFirstCode(getValidation().checkSelfCounterFirstUnicode());
        getValidation().setSecondCode(getValidation().checkSelfCounterSecondUnicode());
        for (int i = 0; i < details.length; i++) {
            String dataType = getColumn().values().toArray()[i].toString();
            String dataName = getColumn().keySet().toArray()[i].toString();
            if (!getValidation().validationRecords(dataName, dataType, details[i], getTree()))
                return false;
        }
        addNewRecord(details, getValidation().getFirstCode(), getValidation().getSecondCode());
        setRecords();
        return true;
    }

    private void addNewRecord(String[] record, double firstUnicode, double secondUnicode) {
        Map<String, String> newRecord = new HashMap<>();
        int counter2 = 0;
        for (String columnName : getColumn().keySet()) {
            newRecord.put(columnName, record[counter2]);
            counter2++;
        }
        if (isUniq()) {
            try {
                getTree().search(firstUnicode);

            } catch (Exception e) {
                getTree().insert(firstUnicode, new HashMap<>());
            }
        }
        //  {}
        ;
        try {
            getTree().search(firstUnicode).put(secondUnicode, newRecord);


        } catch (Exception e) {
            getTree().insert(firstUnicode, new HashMap<>());
            getTree().search(firstUnicode).put(secondUnicode, newRecord);
        }

        setRecords();
    }

    public ArrayList<Map<String, String>> getRecords() {
        return records;
    }

    public void setRecords() {
        getRecords().clear();
        for (Map<Double, Map<String, String>> map : getTree().getCollection())
            for (Map<String, String> record : map.values())
                getRecords().add(record);
    }
}
