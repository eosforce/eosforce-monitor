package client.domain;

/**
 * @author lxg
 * @create 2018-07-06 18:46
 * @desc
 */
public class GenesisAccount {
    private String name;
    private String asset;
    private String key;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
