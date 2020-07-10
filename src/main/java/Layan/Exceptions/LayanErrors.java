package Layan.Exceptions;

public class LayanErrors {

    public static void throwError(String errorMessage){
        System.out.println("Layan Error: " + errorMessage);
        System.exit(0);
    }
}
