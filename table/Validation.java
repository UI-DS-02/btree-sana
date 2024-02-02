package table;

import b_plus_tree.BPlusTree;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {
    private String firstUnicodeType = "";
    private String secondUnicodeType = "";
    private double firstCode;
    private double secondCode;
    private boolean selfCounter;
    private boolean uniq;
    private int counter;
    private ArrayList<String> dataTypeNames;

    private final double fixedCode = 1;

    public Validation(String firstUnicodeType, String secondUnicodeType, boolean selfCounter, boolean uniq) {
        this.firstUnicodeType = firstUnicodeType;
        this.secondUnicodeType = secondUnicodeType;
        this.selfCounter = selfCounter;
        this.uniq = uniq;
        this.counter = 0;
        this.dataTypeNames = new ArrayList<>();
        setDataTypeNames();

    }

    public ArrayList<String> getDataTypeNames() {
        return dataTypeNames;
    }

    public void setDataTypeNames() {
        getDataTypeNames().add("Byte");
        getDataTypeNames().add("Boolean");
        getDataTypeNames().add("Short");
        getDataTypeNames().add("UnsignedInt");
        getDataTypeNames().add("Double");
        getDataTypeNames().add("Integer");
        getDataTypeNames().add("Character");
        getDataTypeNames().add("Mobile");
        getDataTypeNames().add("Phone");
        getDataTypeNames().add("Time");
        getDataTypeNames().add("String");
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public boolean isUniq() {
        return uniq;
    }

    public void setUniq(boolean uniq) {
        this.uniq = uniq;
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

    public double getFixedCode() {
        return fixedCode;
    }

    public double getFirstCode() {
        return firstCode;
    }

    public void setFirstCode(double firstCode) {
        this.firstCode = firstCode;
    }

    public double getSecondCode() {
        return secondCode;
    }

    public void setSecondCode(double secondCode) {
        this.secondCode = secondCode;
    }

    //=============================add validation=====================================
    public boolean validationRecords(String dataName, String dataType, String data, BPlusTree tree) {
        try {
            if (dataType.equals("Integer"))
                Integer.parseInt(data);
            else if (dataType.equals("Double"))
                Double.parseDouble(data);

            else if (dataType.equals("UnsignedInt"))
                Integer.parseUnsignedInt(data);

            else if (dataType.equals("Short"))
                Short.parseShort(data);

            else if (dataType.equals("Boolean"))
                Boolean.parseBoolean(data);

            else if (dataType.equals("Byte"))
                Byte.parseByte(data);
        } catch (NumberFormatException e) {
            return false;
        }
        if (dataType.equals("Character")) {
            if (data.length() != 1)
                return false;
        }
        if (dataType.equals("Date"))
            return timeValidate(data);
        if (dataType.equals("Time"))
            return timeValidate(data);
        if (dataType.equals("Phone"))
            return phoneValidate(data);
        if (dataType.equals("Mobile"))
            return mobileValidate(data);


        if (!isSelfCounter()) {
            if (dataName.equals(getFirstUnicodeType())) {
                setFirstCode(makeAUnicode(data, dataType));
                if (isUniq()) {
                    try {
                        tree.search(getFirstCode());
                        return false;
                    } catch (Exception e) {

                    }
                }
            } else if (dataName.equals(getSecondUnicodeType())) {
                setSecondCode(makeAUnicode(data, dataType));
                try {
                    for (Map<Double, Map<String, String>> map : tree.getCollection())
                        if (map.containsKey(data))
                            return false;
                } catch (Exception e) {
                    return true;
                }
            }
        }


        return true;
    }

    //==============================unicode validation=========================
    public double checkSelfCounterFirstUnicode() {
        if (isSelfCounter()) {
            setCounter(getCounter() + 1);
            return getCounter();
        } else {
            return 0;
        }
    }

    public double checkSelfCounterSecondUnicode() {
        if (isUniq()) {
            return getFixedCode();
        } else {
            return 0;
        }
    }

    private double makeAUnicode(String value, String type) {
        if (type.equals("Integer") || type.equals("Double") || type.equals("Long") || type.equals("Byte") || type.equals("UnsignedInt") || type.equals("Phone") || type.equals("Mobile") || type.equals("Short"))
            return Double.parseDouble(value);
        else if (type.equals("Date") || type.equals("Time")) {
            value.replaceFirst("/", "");
            value.replaceFirst("/", "");
            value.replaceFirst(":", "");
            value.replaceFirst(":", "");
        } else if (type.equals("String")) {
            return Double.parseDouble(String.valueOf(value.hashCode()));
        }
        return Double.parseDouble(value);
    }

    //============================data validation==========================
    private boolean timeValidate(String time) {
        String timeRejex = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$  ";
        Pattern timePattern = Pattern.compile(timeRejex);
        Matcher timeMatcher = timePattern.matcher(time);
        return timeMatcher.matches();
    }

    private boolean dateValidate(String date) {
        String dateRejex = "\\d{4}-\\d{2}-\\d{2}";
        Pattern datePattern = Pattern.compile(dateRejex);
        Matcher dateMatcher = datePattern.matcher(date);
        return dateMatcher.matches();
    }

    private boolean phoneValidate(String phone) {
        String phoneRejex = "^0\\d{10}$";
        Pattern phonePattern = Pattern.compile(phoneRejex);
        Matcher phoneMatcher = phonePattern.matcher(phone);
        return phoneMatcher.matches();
    }

    private boolean mobileValidate(String mobile) {
        String mobileRejex = "^09\\d{8}$";
        Pattern mobilePattern = Pattern.compile(mobileRejex);
        Matcher mobileMatcher = mobilePattern.matcher(mobile);
        return mobileMatcher.matches();
    }

    //===========================edit function=================================
    public boolean validateEditation(double firstCode, double secondCode, String column, String dataType, String data, BPlusTree tree) {
        try {
            tree.search(firstCode);
        } catch (Exception e) {
            return false;
        }

        if (tree.search(firstCode).containsKey(secondCode) && validationRecords(column, dataType, data, tree))
            return true;

        return false;
    }

    public boolean addNewColumn(String columnName, String dataType, Map<String, String> column) {
        return (!column.containsKey(columnName) && getDataTypeNames().contains(dataType));
    }

    //=====================remove function======================
    public boolean removeColumn(String columnName) {
        if ((columnName.equals(getFirstUnicodeType()) || columnName.equals(getSecondUnicodeType())))
            return false;
        return true;
    }

    public boolean removeRecord(double firstUnicode, double secondUnicode, BPlusTree tree) {
        try {
            tree.search(firstUnicode);
        } catch (Exception e) {

            return false;
        }
        if (tree.search(firstUnicode).containsKey(secondUnicode)) {
            return true;
        }
        return false;
    }

    public boolean removeRecord(double firstUnicode, BPlusTree tree) {
        try {
            tree.search(firstUnicode);

        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
