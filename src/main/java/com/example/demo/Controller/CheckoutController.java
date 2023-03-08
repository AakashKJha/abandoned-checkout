package com.example.demo.Controller;
import com.example.demo.Service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.util.Map;

@RestController
@RequestMapping("/checkout")
public class CheckoutController {
    private final Integer FIRST = 30;
    private final Integer SECOND = 1;
    private final Integer THIRD = 3;
    @Autowired
    CheckoutService checkoutService;
    @GetMapping("/details")
    public ModelAndView getAll(){
        ModelAndView mav = new ModelAndView("checkout");
        mav.addObject("orders",checkoutService.getAllOrders());
        mav.addObject("messages",checkoutService.getAllMessage());

        return mav;

    }

    @PostMapping("/")
    public ResponseEntity<String> getAllTarget(@RequestBody Map<String, Object> request,
                                               @RequestParam(name = "first", required = false) Integer first,
                                               @RequestParam(name = "second", required = false) Integer second,
                                               @RequestParam(name = "third", required = false) Integer third){
        if(request.get("order")!=null){
            checkoutService.removeScheduled((Map<String, Object>) request.get("order"));
        }
        else{
            Integer firstEventTime = (first==null)?FIRST:first;
            Integer secondEventTime = (second==null)?SECOND:second;
            Integer thirdEventTime = (third==null)?THIRD:third;
            checkoutService.addAbandoned(request,firstEventTime,secondEventTime,thirdEventTime);
        }

        return new ResponseEntity<>("Success",HttpStatus.OK);

    }
}
