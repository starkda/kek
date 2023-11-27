package com.example.kek.semantic.util;

public class Converter {
    public int convertBooleanToInt(boolean bool){
        if(bool)
            return 1;
        else return 0;
    }
    public boolean convertIntToBoolean(int integer) throws Exception {
        if(integer == 1)
            return true;
        else if(integer == 0)
            return false;
        else
            throw new Exception("Error: error in semantic of program (Converter.convertIntToBoolean) \n" +
                    ", expected 1 or 0 , but receive:" + integer);
    }

}
