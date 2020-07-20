package converter;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        try {
            int sourceRadix = Integer.parseInt(scanner.nextLine());
            String sourceNumber = scanner.nextLine();
            int targetRadix = Integer.parseInt(scanner.nextLine());

            NumberSystemConverter converter = new NumberSystemConverter(sourceRadix, sourceNumber, targetRadix);
            System.out.println(converter.convert());
        } catch(Exception e) {
            System.out.println("error : "+e.getMessage());
        }
    }
}

class NumberSystemConverter {
    public static final char[] SYMBOLS = new char[36];
    static {
        int count = 0;
        for(char c='0'; c <= '9'; c++) {
            SYMBOLS[count++] = c;
        }
        for(char c='a'; c<='z'; c++) {
            SYMBOLS[count++] = c;
        }
    }

    private int sourceRadix;
    private int targetRadix;
    private String sourceNumber;
    private StringBuilder outputNumber;
    private boolean containsFractionalPart;

    public NumberSystemConverter(int sourceRadix, String sourceNumber, int targetRadix) {
        this.sourceRadix = sourceRadix;
        this.sourceNumber = sourceNumber;
        this.containsFractionalPart = sourceNumber.indexOf('.') != -1;
        this.targetRadix = targetRadix;
        this.outputNumber = new StringBuilder();
    }

    public void checkIfSourceInBaseOneContainsOnlyOne() {
        for(char c : sourceNumber.toCharArray()) {
            if(c != '1' && c != '.')
                throw new RuntimeException("Invalid source number of base one - does not contain only ones");
        }
    }

    public void checkIfInputContainsZeroOrOnePeriod() {
        int count = 0;
        for(char c : sourceNumber.toCharArray()) {
            if(c == '.')
                count++;
        }
        if(!(count == 0 || count == 1))
            throw new RuntimeException("Invalid number of periods in the input - can contain utmost one period");
    }

    public void checkIfSourceSymbolsMatchRadix() {
        for(char c : sourceNumber.toCharArray()) {
            int index = 0;
            if(Character.isAlphabetic(c)) {
                if(Character.isUpperCase(c))
                    throw new RuntimeException("Symbol in the string does not match case");
                index = c-'a' + 10;
            } else if(!Character.isDigit(c) && c != '.')
                throw new RuntimeException("Symbol in the string is special and is invalid");
            else
                index = c-'0';
            if(index >= sourceRadix)
                throw new RuntimeException("Symbol in the string does not conform to the radix");
        }
    }


    public void checkValidInputs() {
        if(sourceRadix > 36 || sourceRadix < 1)
            throw new RuntimeException("Source Radix invalid: must not be less than 1 or greater than 36");
        if(targetRadix > 36 || targetRadix < 1)
            throw new RuntimeException("Target Radix invalid: must not be less than 1 or greater than 36");
        checkIfInputContainsZeroOrOnePeriod();
        if(sourceRadix==1)
            checkIfSourceInBaseOneContainsOnlyOne();
        else checkIfSourceSymbolsMatchRadix();
    }

    public String getStringOfOnes(long count) {
        char c = '1';
        String result = "";
        for( int i = 0; i < count; i++) {
            result += c;
        }
        return result;
    }


    public void convertIntegerPart() {
        long resultInDecimal = 0L;
        String sourceInteger = containsFractionalPart ?
                sourceNumber.substring(0, sourceNumber.indexOf('.')) : sourceNumber;
        String resultAsString;
        if(sourceRadix == 1) {
            checkIfSourceInBaseOneContainsOnlyOne();
            resultInDecimal = sourceInteger.length();
        } else
            resultInDecimal = Long.parseLong(sourceInteger, sourceRadix);
        if(targetRadix == 1) {
            resultAsString = getStringOfOnes(resultInDecimal);
        } else {
            resultAsString = Long.toString(resultInDecimal, targetRadix);
        }
        outputNumber.append(resultAsString);
        if(containsFractionalPart)
            outputNumber.append(".");
    }

    public double convertFractionalPartIntoDecimal(String sourceFraction) {
        sourceFraction = sourceFraction.substring(1);
        int counter = 1;
        double fraction = 0.0;
        for(char c : sourceFraction.toCharArray()) {
            int digit = Character.isDigit(c) ? c-'0' : (c-'a'+10);
            fraction += Double.valueOf(digit) / Math.pow((double) sourceRadix, (double) counter);
            counter++;
        }
        return fraction;
    }

    public void convertFractionalPart() {
        String sourceFraction = this.sourceNumber.substring(sourceNumber.indexOf('.'));
        double fraction = sourceRadix == 1 ?
                Double.parseDouble(Integer.toString(sourceFraction.length())) :
                convertFractionalPartIntoDecimal(sourceFraction);

        int count = 0;
        while( count < 5 ) {
            count++;
            fraction = fraction * targetRadix;
            int digit = (int) fraction;
            fraction-= digit;
            outputNumber.append(SYMBOLS[digit]);
        }
    }


    public String convert() {
        checkValidInputs();
        convertIntegerPart();
        if(containsFractionalPart)
            convertFractionalPart();
        return outputNumber.toString();
    }
}

enum Format {
    BIN(2), OCT(8), HEX(16);
    private int radix;
    private String prefix;
    Format(int radix) {
        this.radix = radix;
    }

    public String getPrefix() {
        switch(radix) {
            case 2:
                return "0b";
            case 8:
                return "0";
            case 16:
                return "0x";
        }
        return "";
    }

    public static Format getValue(int radix) {
        switch(radix) {
            case 2:
                return BIN;
            case 8:
                return OCT;
            case 16:
                return HEX;
        }
        return null;
    }


}
