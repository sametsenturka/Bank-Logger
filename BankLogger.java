// @author Samet SENTURK
// @date 08.12.2024
// @version 4.1

// Before executing the code. Delete the AccountInfoOut.txt & User.log files.
// Then you can check if the logging process was done correct.

import java.io.*;
import java.util.Scanner;

public class BankLogger {
    public static void main(String[] args) throws Exception {

        int[] acctNums = new int[countAccounts(args[0] + "_AccountInfo.txt")];
        String[] acctNames = new String[countAccounts(args[0] + "_AccountInfo.txt")];
        String[] acctSurnames = new String[countAccounts(args[0] + "_AccountInfo.txt")];
        double[] acctBalances = new double[countAccounts(args[0] + "_AccountInfo.txt")];

        int numOfAccounts = countAccounts(args[0] + "_AccountInfo.txt");
        readAccountInfo(acctNums, acctNames, acctSurnames, acctBalances, args[0] + "_AccountInfo.txt");

        processTransfers(acctNums, acctBalances, args[0] + "_TransferInfo.txt", args[0] + ".log");
        processBillPay(acctNums, acctBalances, args[0] + "_BillPay.txt", args[0] + ".log");

        writeAccountInfo(acctNums, acctNames, acctSurnames, acctBalances, args[0] + "_AccountInfoOut.txt");
    }

    public static int countAccounts(String filename) throws Exception {
        Scanner input = new Scanner(new File(filename));
        int numOfAccounts = 0;
        while (input.hasNext()) {
            input.nextLine();
            numOfAccounts++;
        }
        input.close();
        return numOfAccounts;
    }

    public static void readAccountInfo(int[] acctNums, String[] names, String[] surnames, double[] balances, String filename) throws FileNotFoundException {
        int i = 0;
        Scanner input = new Scanner(new File(filename));

        while (input.hasNext()) {
            acctNums[i] = input.nextInt();
            names[i] = input.next();
            surnames[i] = input.next();
            balances[i] = input.nextDouble();
            ++i;
        }
        input.close();
    }

    public static void writeAccountInfo(int[] acctNums, String[] names, String[] surnames, double[] balances, String filename) throws Exception {
        File file = new File(filename);
        PrintWriter output = new PrintWriter(file);

        for (int i = 0; i < acctNums.length; i++) {
            output.print(acctNums[i] + " ");
            output.print(names[i] + " ");
            output.print(surnames[i] + " ");
            output.println(balances[i]);
        }
        output.close();
    }

    public static boolean deposit(double[] balances, int index, double amount) {
        if (index < 0 || index >= balances.length) {
            return false;
        }
        if (isDepositValid(amount)) {
            balances[index] = balances[index] + amount;
            return true;
        }
        return false;
    }

    public static boolean withdrawal(double[] balances, int index, double amount) {
        if (index < 0 || index >= balances.length) {
            return false;
        }
        if (isWithdrawalValid(balances[index], amount)) {
            balances[index] = balances[index] - amount;
            return true;
        }
        return false;
    }

    public static int transfer(int[] acctNums, double[] balances, int acctNumFrom, int acctNumTo, double amount) {
        int indexOfFrom = findAcct(acctNumFrom, acctNums);
        int indexOfTo = findAcct(acctNumTo, acctNums);

        if (indexOfFrom >= 0 && balances[indexOfFrom] >= amount && indexOfTo >= 0) {
            balances[indexOfFrom] -= amount;
            balances[indexOfTo] += amount;
            return 0;
        }
        else if (indexOfTo == -1)
            return 3;
        else if (indexOfFrom == -1)
            return 2;
        else
            return 1;
    }

    public static int findAcct(int acctNum, int[] acctNums) {
        for (int i = 0; i < acctNums.length; i++) {
            if (acctNums[i] == acctNum) {
                return i;
            }
        }
        return -1;
    }

    public static void processTransfers(int[] acctNums, double[] acctBalances, String transferFile, String logFile) throws Exception {
        Scanner input = new Scanner(new File(transferFile));
        PrintWriter log = new PrintWriter(new FileWriter(logFile, true));

        while (input.hasNext()) {
            String transferNumber = input.next();
            int acctFrom = input.nextInt();
            int acctTo = input.nextInt();
            double amount = input.nextDouble();

            int result = transfer(acctNums, acctBalances, acctFrom, acctTo, amount);
            log.println("Transfer " + transferNumber + " resulted in code " + result + ": " + transferresult(result));
        }
        input.close();
        log.close();
    }


    public static void processBillPay(int[] acctNums, double[] acctBalances, String billPayFile, String logFile) throws Exception {
        Scanner input = new Scanner(new File(billPayFile));
        PrintWriter log = new PrintWriter(new FileWriter(logFile, true));

        while (input.hasNext()) {
            String billPayNumber = input.next();
            int acctFrom = input.nextInt();
            String billType = input.next();
            double amount = input.nextDouble();

            int indexOfFrom = findAcct(acctFrom, acctNums);
            int resultCode;

            if (indexOfFrom >= 0 && withdrawal(acctBalances, indexOfFrom, amount)) {
                resultCode = 0;
            } else {
                resultCode = (indexOfFrom == -1) ? 2 : 1;
            }

            log.println("Bill Pay " + billPayNumber + " resulted in code " + resultCode + ": " + billresult(resultCode));
        }
        input.close();
        log.close();
    }

    public static String transferresult(int code) {

        // yeni switch-case yontemi.
        return switch (code) {
            case 0 -> "STX - Transfer Successful";
            case 1 -> "TNF - To Account not found";
            case 2 -> "FNF - From Account not found";
            case 3 -> "NSF - Insufficient Funds";
            default -> "STX - Transfer Successful";
        };
    }

    public static String billresult(int code) {

        return switch (code) {
            case 0 -> "STX - Payment Successful";
            case 4 -> "TNF - To Account not found";
            case 2 -> "FNF - From Account not found";
            case 1 -> "NSF - Insufficient Funds";
            default -> "STX - Payment Successful";
        };
    }

    public static boolean isDepositValid(double amount) {
        return amount > 0;
    }

    public static boolean isWithdrawalValid(double balance, double amount) {
        return amount > 0 && amount <= balance;
    }
}
