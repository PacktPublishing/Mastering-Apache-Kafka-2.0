public class PharmaClass {
    private String country;
    private String time;
    private String pcnt_hlth_exp;
    private String pcnt_gdp;
    private String usd_cap;
    private String flag_codes;
    private String total_spend;
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getPcnt_hlth_exp() {
        return pcnt_hlth_exp;
    }
    public void setPcnt_hlth_exp(String pcnt_hlth_exp) {
        this.pcnt_hlth_exp = pcnt_hlth_exp;
    }
    public String getPcnt_gdp() {
        return pcnt_gdp;
    }
    public void setPcnt_gdp(String pcnt_gdp) {
        this.pcnt_gdp = pcnt_gdp;
    }
    public String getUsd_cap() {
        return usd_cap;
    }
    public void setUsd_cap(String usd_cap) {
        this.usd_cap = usd_cap;
    }
    public String getFlag_codes() {
        return flag_codes;
    }
    public void setFlag_codes(String flag_codes) {
        this.flag_codes = flag_codes;
    }
    public String getTotal_spend() {
        return total_spend;
    }
    public void setTotal_spend(String total_spend) {
        this.total_spend = total_spend;
    }
    @Override
    public String toString() {
        return "PharmaClass [country=" + country + ", time=" + time + ", pcnt_hlth_exp=" + pcnt_hlth_exp + ", pcnt_gdp="
                + pcnt_gdp + ", usd_cap=" + usd_cap + ", flag_codes=" + flag_codes + ", total_spend=" + total_spend
                + "]";
    }

}