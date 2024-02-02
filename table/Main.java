package table;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class Main {
    public static void main(String args[]) {
        Scanner s = new Scanner(System.in);
        Map<String,Table> tables=new HashMap<>();
        while (true) {
   //         String order=s.nextLine();
//            if (order.equals("new table")){
//
//                System.out.println("please enter table name");
//                String tableName=s.nextLine();
//                while (tables.containsKey(tableName)){
//                    System.out.println("this table exist before");
//                    tableName=s.nextLine();}
//                Table table=new Table(tableName);
//                tables.put(tableName,table);
//                System.out.println("please enter number of columns");
//                int num=Integer.parseInt(s.nextLine());
//                for (int i = 0; i < num; i++) {
//                    System.out.println("please enter type of column "+(i+1));
//                    String columnType=s.nextLine();
//                    System.out.println("please enter name of column "+(i+1));
//                    while (!table.validateColumn(s.nextLine(),columnType)){
//                        System.out.println("this column exist before");
//                    }
//                }
//                System.out.println("do you have code to identify rows\n1) yes\n2) no");
//                String answer=s.nextLine();
//                if (answer.equals("2")){
//                    table.setUniq(true);
//                    table.setSelfCounter(true);}
//                if (answer.equals("1")){
//                    System.out.println("please enter unicode name");
//                    table.setFirstUnicodeType(s.nextLine());
//                    System.out.println("is your code uniq?\n1) yes\n2) no");
//                    String answerUniq=s.nextLine();
//                    if (answerUniq.equals("1")){
//                        table.setUniq(true);
//                    }
//                    else {
//                        System.out.println("please enter an second uniq code");
//                        table.setSecondUnicodeType(s.nextLine());
//                    }
//                    table.newValidation();
//                    System.out.println("your table has been made successfully!");
//                }
//            }
            Table table=new Table("a");
            table.getColumn().put("name","String");
            table.getColumn().put("gpa","Double");
            table.setUniq(false);
            table.setSelfCounter(false);
            table.getColumn().put("id","Integer");
            table.setFirstUnicodeType("gpa");
            table.setSecondUnicodeType("id");
            tables.put("a",table);
            table.newValidation();
            table.addRecords("zahra 11.2 100");
            table.addRecords("sana 11.2 105");
            table.addRecords("hasan 11.2 107");
            table.addRecords("ali 13.1 110");
            table.addRecords("kimia 13.1 113");
            table.addRecords("reza 6 120");
//            if (order.equals("edit table")){
//                System.out.println("please enter table name");
//                String tableName=s.nextLine();
//                while (!tables.containsKey(tableName)){
//                    System.out.println("this table doesnt exist ! try again");
//                    tableName=s.nextLine();}
                table=tables.get("a");
                String editOrder=s.nextLine();
                while (true) {

                    if (editOrder.equals("add record")) {
//                    for (String detail:table.getColumn().keySet())
                        //System.out.print(detail+"("+table.getColumn().get(detail)+") ");
                        //   String newRecord=s.nextLine();
                        while (!table.addRecords(s.nextLine())) {
                            System.out.println("try again!");
                        }
                    }
                    if (editOrder.equals("search uniq record")) {
                        String search1 = s.nextLine();
                        System.out.println(table.searchUniqRecords(Double.parseDouble(search1.split(" ")[0]), Double.parseDouble(search1.split(" ")[1])));
                    }
                    if (editOrder.equals("search records")) {
                        System.out.println(table.searchRecords(Double.parseDouble(s.nextLine())));
                    }
                    if (editOrder.equals("search in range")) {
                        String search1 = s.nextLine();
                        System.out.println(table.rangeSearch(Double.parseDouble(search1.split(" ")[0]), Double.parseDouble(search1.split(" ")[1])));
                    }
                    if (editOrder.equals("search a feature")) {
                        String search1 = s.nextLine();
                        System.out.println(table.spcialRecords(search1.split(" ")[0], search1.split(" ")[1]));
                    }

                    if (editOrder.equals("print")) {

                        System.out.println(table.printAllRecords());
                    }
                    if (editOrder.equals("remove record")) {
                        String search1 = s.nextLine();
                        while (!table.removeRecord(Double.parseDouble(search1.split(" ")[0]), Double.parseDouble(search1.split(" ")[1]))) {
                            System.out.println("try again!");
                        }
                    }
                    if (editOrder.equals("edit record")) {
                        String search1 = s.nextLine();
                        while (!table.editRecord(Double.parseDouble(search1.split(" ")[0]), Double.parseDouble(search1.split(" ")[1]), search1.split(" ")[2], search1.split(" ")[3]))
                            System.out.println("try again!");
                    }
                    if (editOrder.equals("add column")) {
                        String search1 = s.nextLine();
                        while (!table.addNewColumn(search1.split(" ")[0], search1.split(" ")[1]))
                            System.out.println("try again!");
                    }
                    if (editOrder.equals("remove column")) {
                        while (!table.removeColumn(s.nextLine()))
                            System.out.println("try again!");
                    }
                    editOrder = s.nextLine();
                }
                }

        }}

