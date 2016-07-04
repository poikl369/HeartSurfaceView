package heart.me.heart;

/**
 * Created by yun.liu@avazu.net on 2016/5/10.
 */
public enum Heart {
    like("like", R.mipmap.icon_heart_7), shock("shock", R.mipmap.icon_shocked), happy("happy", R.mipmap.icon_happy), neutral("neutral", R.mipmap.icon_neutral), tongue("tongue", R.mipmap.icon_tongue);

    public String key;
    public int drawableId;

    Heart(String key, int drawableId) {
        this.key = key;
        this.drawableId = drawableId;
    }

    public static int getDrawableId(String key) {
        for (Heart heart : Heart.values()) {
            if (heart.key.equals(key)) {
                return heart.drawableId;
            }
        }
        return 0;
    }

}
