import java.util.List;

public class GetCountry {
    private String postCode;
    private String country;
    private String countryAbbreviation;
    private List<Places> Places;

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryAbbreviation() {
        return countryAbbreviation;
    }

    public void setCountryAbbreviation(String countryAbbreviation) {
        this.countryAbbreviation = countryAbbreviation;
    }

    public List<Places> getPlaces() {
        return Places;
    }

    public void setPlaces(List<Places> places) {
        Places = places;
    }
}
