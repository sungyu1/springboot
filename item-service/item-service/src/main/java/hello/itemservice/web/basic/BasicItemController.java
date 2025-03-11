package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model){
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "basic/items";
    }
    @GetMapping("/{itemId}")
    public String item(@PathVariable("itemId") long itemId, Model model){
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/item";

        }
    @GetMapping("/add")
    public String addForm(){
        return "basic/addForm";
    }

//  저장
//    @PostMapping("/add")
    public String additemV1(@RequestParam ("itemName")String itemName,
                       @RequestParam ("price")int price,
                       @RequestParam ("quantity")Integer quantity,
                        Model model){

        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        itemRepository.save(item);

        model.addAttribute("item", item);

        return "basic/item";
    }

//    @PostMapping("/add")
    public String additemV2(@ModelAttribute("item") Item item){

        itemRepository.save(item);

//        model.addAttribute("item", item); @ModelAttibute 로 선언했을경우 자동추가 되는 부분 생략가능.

        return "basic/item";
    }

//    @ModelAttribute ()괄호를 없앴을경우
//    @PostMapping("/add")
    public String additemV3(@ModelAttribute Item item){

        itemRepository.save(item);

//        model.addAttribute("item", item); @ModelAttibute 로 선언했을경우 자동추가 되는 부분 생략가능.

        return "basic/item";
    }

//    @PostMapping("/add")
    public String additemV4( Item item){
        itemRepository.save(item);
        return "basic/item";
    }

//    @PostMapping("/add")
    public String additemV5(Item item){
        itemRepository.save(item);
        return "redirect:/basic/items/"+item.getId();
    }

    @PostMapping("/add")
    public String additemV6(Item item, RedirectAttributes redirectAttributes){
        Item saveItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId",saveItem.getId());
        redirectAttributes.addAttribute("status",true);
        return "redirect:/basic/items/{itemId}";
    }



//    상품수정
    @GetMapping("/{itemId}/edit")
    public String ediForm(@PathVariable("itemId") Long itemId, Model model){
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable ("itemId")Long itemId, @ModelAttribute Item item){
        itemRepository.update(itemId, item);
        return "redirect:/basic/items/{itemId}";
    }

    /*테스트용 데이터 추가 */
    @PostConstruct
    public void init(){
        itemRepository.save(new Item("itemA", 10000,10));
        itemRepository.save(new Item("itemB", 20000,20));

    }
}
