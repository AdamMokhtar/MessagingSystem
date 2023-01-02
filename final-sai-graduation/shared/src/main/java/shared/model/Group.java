package shared.model;

public enum Group {
    SOFTWARE, TECHNOLOGY;

    public String shortName(){
        return name().substring(0,4);
    }
}
