/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test_case;

/**
 *
 * @author ms
 */
class Relation {

    private String Symbol;
    private String ActivityName;
    private String SequendeNumber;
    private String Dependency;

    public Relation(String Symbol, String ActivityName, String SequenceNumber, String Dependency) {

        this.Symbol=Symbol;
        this.ActivityName = ActivityName;
        this.SequendeNumber = SequenceNumber;
        this.Dependency = Dependency;

    }

    @Override
    public String toString() {

       return "<" + Symbol + ", " + ActivityName + ", " +SequendeNumber + ", " + Dependency + ">";
   //     return Symbol +","+Dependency;
    }
    
    public String toString1(){
        return Dependency+" "+Symbol;
    }
    public String toString2(){
        return Dependency+" "+SequendeNumber;
    }
}