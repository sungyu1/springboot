package sungyu.itemservice.web.basic;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sungyu.itemservice.domain.item.Item;
import sungyu.itemservice.domain.item.ItemRepository;

import java.util.List;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {

    private final ItemRepository itemRepository;

//    Item 목록 출력
    @GetMapping
    public String items(Model model){
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "basic/items";
    }
//  등록하는 form으로 이동
    @GetMapping("/add") // 먼저 선언
    public String addForm() {
        return "basic/addForm"; // 템플릿도 필요
    }
//  저장하고 addFoem 으로 이동
//  addForm에서 입력받은값을 @RequestParam 이선언한 변수명에 저장
//    @PostMapping("/add")
    public String addItemv1(@RequestParam("itemName") String itemName,
                       @RequestParam("price") int price,
                       @RequestParam("quantity") Integer quantity,
                        Model model) {
        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);
//        저장한내용
        itemRepository.save(item);
        model.addAttribute("item", item);

        return "basic/item";
    }
//위에 있는 addItemv1 보다 좀더 함축적으로 만든 메서드
//    @PostMapping("/add")
    public String addItemv2(@ModelAttribute("item")Item item, Model model) {

//        저장한내용
        itemRepository.save(item);
        model.addAttribute("item", item); // 자동 추가, 생략 가능

        return "basic/item";
    }
    //위에 있는 addItemv2 보다 좀더 함축적으로 만든 메서드
//    @PostMapping("/add")
    public String addItemv3(@ModelAttribute("item") Item item){
//        저장한내용
        itemRepository.save(item);
        return "basic/item";
    }
    //위에 있는 addItemv3 보다 좀더 함축적으로 만든 메서드
//    @PostMapping("/add")
    public String addItemv4(@ModelAttribute Item item, Model model){
//        저장한내용
        itemRepository.save(item);

        return "basic/item";
    }
    //위에 있는 addItemv4 보다 좀더 함축적으로 만든 메서드
//    @PostMapping("/add")
    public String addItemv5(Item item){
//        저장한내용
        itemRepository.save(item);

        return "basic/item";
    }

//    @PostMapping("/add")
    public String addItemv6(Item item){
//        저장한내용
        itemRepository.save(item);

        return "redirect:/basic/items/" + item.getId();
    }

    @PostMapping("/add")
    public String addItemv7(Item item, RedirectAttributes redirectAttributes){
//        저장한내용
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId",savedItem.getId());
        redirectAttributes.addAttribute("status",true);
        return "redirect:/basic/items/{itemId}";
    }


//    Item 상품 조회
    @GetMapping("/{itemId}")
    public String item(@PathVariable("itemId") long itemId, Model model){
        Item item=itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/item";
    }

//    Item 상품 수정 이동 메서드
    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable("itemId") long itemId, Model model){
        Item item=itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/editForm";
    }
//  Item 상품 수정 저장 하는 메서드
    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable("itemId") long itemId, @ModelAttribute Item item){
        itemRepository.update(itemId, item);
        return "redirect:/basic/items/{itemId}";
    }

//  test용 데이터 추가함.
    @PostConstruct
    public void init(){
        itemRepository.save(new Item("itemA", 10000,10));
        itemRepository.save(new Item("itemB", 20000,30));
        itemRepository.save(new Item("itemC", 70000,50));

    }

}
