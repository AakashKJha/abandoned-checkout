package com.example.demo.Controller;
import com.example.demo.Service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/checkout")
public class CheckoutController {
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
    public ResponseEntity<String> getAllTarget(@RequestBody Map<String, Object> request){
        if(request.get("order")!=null){
            checkoutService.removeScheduled((Map<String, Object>) request.get("order"));
        }
        else{
            Map<String,Object>remainder =(request.get("remainder")!=null)?(Map<String, Object>) request.get("remainder"):new HashMap<>();
            int no =(remainder.get("no")!=null)?Integer.valueOf(remainder.get("no").toString()):3;
            List<String> mess =(remainder.get("message")!=null)?(List<String>) remainder.get("message"):new ArrayList<>();
            List<Integer>time =(remainder.get("remaindertime")!=null)? (List<Integer>)remainder.get("remaindertime"):new ArrayList<>();
            if (mess.isEmpty()){
                mess.add("This is first message.");
                mess.add("This is second message.");
                mess.add("This is third message.");
            }
            if(time.isEmpty()){
                time.add(1);
                time.add(2);
                time.add(3);
            }




            checkoutService.addAbandoned(request,mess,time,no);
        }

        return new ResponseEntity<>("Success",HttpStatus.OK);

    }
}
