package om.example.om_pay.dto.request;

public class ResendCodeSecretRequest {
    private String telephone;

    public ResendCodeSecretRequest() {}

    public ResendCodeSecretRequest(String telephone) {
        this.telephone = telephone;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
