package hi.buyphotocard.hbpApp;


public class ScoreList {
   int deliveryScore;
   int mannerScore;
   int itemScore;

   public ScoreList(){}


    public int getDeliveryScore() {
        return deliveryScore;
    }

    public void setDeliveryScore(int deliveryScore) {
        this.deliveryScore = deliveryScore;
    }

    public int getMannerScore() {
        return mannerScore;
    }

    public void setMannerScore(int mannerScore) {
        this.mannerScore = mannerScore;
    }

    public int getItemScore() {
        return itemScore;
    }

    public void setItemScore(int itemScore) {
        this.itemScore = itemScore;
    }

    public ScoreList(int deliveryScore, int mannerScore, int itemScore){
       this.deliveryScore = deliveryScore;
       this.itemScore = itemScore;
       this.mannerScore = mannerScore;
    }

}