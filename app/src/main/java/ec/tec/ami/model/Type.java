package ec.tec.ami.model;

public enum Type {

    TEXT("TEXT"), PHOTO("PHOTO"), VIDEO("VIDEO");

    private String text;

    Type(String text){
        this.text=  text;
    }

    @Override
    public String toString(){
        return  text;
    }
}