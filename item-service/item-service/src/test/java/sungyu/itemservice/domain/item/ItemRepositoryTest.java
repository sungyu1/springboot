package sungyu.itemservice.domain.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


class ItemRepositoryTest {

    ItemRepository itemRepository = new ItemRepository();

//    test 가 끝날때 마다 test 한 정보를 지워야 다음 테스트 에 영향이 없으므로 아래 메서드 실행
    @AfterEach
    void afterEach(){
        itemRepository.clearStore();
    }
    @Test
    void save(){
//        giveen
    Item item=new Item("itemA",1000,10);
//        when (item 에 추가한 내용을 itemRepository.save에 저장)
    Item saveItem=itemRepository.save(item);
//        then (itemRepository.findById(item.getId())에서 saveItem이 잘저장되었는지 확인)
//        then (Assertions.assertThat(findItem).isEqualTo(saveItem) 를 사용해서 findItem 과 saveItem이 똑같은지 확인
    Item findItem=itemRepository.findById(item.getId());
    assertThat(findItem).isEqualTo(saveItem);
    }
    @Test
    void findAll(){
//        giveen
        Item item1=new Item("item1",10000,10);
        Item item2=new Item("item2",20000,20);
        Item item3=new Item("item3",70000,30);

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

//        when
    List<Item> result=itemRepository.findAll();

//        then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result).contains(item1,item2,item3);

    }

    @Test
    void updateItem(){
//        giveen
        Item item=new Item("item1",10000,10);

        Item saveItem=itemRepository.save(item);
        Long itemId= saveItem.getId();

//        when
        Item updateParam= new Item("item2",20000,30);
        itemRepository.update(itemId, updateParam);
        Item findItem= itemRepository.findById(itemId);
//        then
        assertThat(findItem.getItemName()).isEqualTo(updateParam.getItemName());
        assertThat(findItem.getPrice()).isEqualTo(updateParam.getPrice());
        assertThat(findItem.getQuantity()).isEqualTo(updateParam.getQuantity());

    }
}