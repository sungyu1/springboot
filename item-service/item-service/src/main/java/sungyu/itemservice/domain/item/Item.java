package sungyu.itemservice.domain.item;


import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Item {

    private  Long id;
    private  String itemName;
    //Integer 을 쓰는 이유는 null 값이어도 저장이 되게 하기 위해서 사용함. Int를 사용할경우 0이라도 들어가야함.
    private  Integer price;
    private  Integer quantity;

//    기본생성자 단축기 Alt + insert
    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
