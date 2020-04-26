package e.hp.uploadimage;

public class HomeStay_Photos {

    private String image;
    private String name;
    private String HPID;

    public HomeStay_Photos(String HS_Image , String HS_Name, String HSID) {
        this.image = HS_Image ;
        this.name = HS_Name;
        this.HPID = HSID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String HS_Image) {
        this.image = HS_Image;
    }

    public String getName() {
        return name;
    }

    public void setName(String HS_Name) {
        this.name = HS_Name;
    }

    public String getHPID() {
        return HPID;
    }

    public void setHPID(String HSID) {
        this.HPID = HSID;
    }
}
