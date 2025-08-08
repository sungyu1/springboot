package sungyu.itemservice.domain.item;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepository {

    private static final Map<Long, Item>store=new HashMap<>(); //static 를 사용
    private static long sequence =0L; //static 를 사용

//     save 하는 메서드
    public Item save(Item item){
        item.setId(++sequence);
        store.put(item.getId(), item);
        return item;
    }

//   id 조회하는 메서드
    public Item findById(Long id){
        return store.get(id);
    }

//   전체 조회 메서드
//   ait + enter 로 인폴트 시켜주기

    public List<Item> findAll(){
    return new ArrayList<>(store.values());
    }

//   업데이트 메서드
    public void update(Long itemId, Item updateParam){
//       아이템 찾기
        Item findItem=findById(itemId);
//       이름 업데이트
        findItem.setItemName(updateParam.getItemName());
//       가격 업데이트
        findItem.setPrice(updateParam.getPrice());
//       수량 업데이트
        findItem.setQuantity(updateParam.getQuantity());

    }
//    store 데이터 전체 삭제
    public void clearStore(){
        store.clear();
    }
}
