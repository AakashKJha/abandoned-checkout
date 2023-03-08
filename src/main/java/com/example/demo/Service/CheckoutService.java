package com.example.demo.Service;

import com.example.demo.Repo.CheckoutRepo;
import com.example.demo.Repo.MessageRepo;
import com.example.demo.entity.Checkout;
import com.example.demo.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CheckoutService {

    @Autowired
    CheckoutRepo checkoutRepo;
    @Autowired
    MessageRepo messageRepo;
    public Checkout getCheckoutByCartId(String id){
        Optional<Checkout> checkout = checkoutRepo.findByCartIdAndOrderPlacedIsFalse(id);
        if(checkout.isPresent()){
            return checkout.get();
        }
        else{
            throw new RuntimeException("No cart found with Id: "+ id);
        }
    }
    public Checkout getCheckoutByCartIdAndNotPlaced(String id){
        Optional<Checkout> checkout = checkoutRepo.findByCartId(id);
        if(checkout.isPresent()){
            return checkout.get();
        }
        else{
            throw new RuntimeException("No cart found with Id: "+ id);
        }
    }
    public Message getMessageByCartId(String id){
        Optional<Message> message = messageRepo.findByCartId(id);
        if(message.isPresent()){
            return message.get();
        }
        else{
            throw new RuntimeException("No cart found with Id: "+ id);
        }
    }

    public void removeScheduled(Map<String, Object> request){
        Checkout checkout;
        if(checkoutRepo.findByCartId(request.get("id").toString()).isPresent()){
            checkout =getCheckoutByCartId(request.get("id").toString());
        }
        else {
            checkout = new Checkout();
            checkout.setCartId( request.get("id").toString());
            Map<String,Object>customer = (Map<String, Object>)request.get("customer");
            checkout.setPhone(customer.get("phone").toString());
        }
        checkout.setOrderPlaced(true);
        checkoutRepo.save(checkout);
    }
    public void addAbandoned(Map<String, Object> request, Integer first, Integer second, Integer third){
        Checkout checkout = new Checkout();
        checkout.setCartId( request.get("id").toString());
        Map<String, Object> phone = (Map<String, Object>) request.get("phone");
        checkout.setPhone(phone.get("phone").toString());
        checkoutRepo.save(checkout);
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(calendar.MINUTE,first);
        Date firstEventTime = calendar.getTime();
        calendar.add(calendar.MINUTE,-first);
        calendar.add(calendar.DAY_OF_MONTH,second);
        Date secondEventTime = calendar.getTime();
        calendar.add(calendar.DAY_OF_MONTH,-second);
        calendar.add(calendar.DAY_OF_MONTH,third);
        Date thirdEventTime = calendar.getTime();
        ArrayList<Date>times = new ArrayList<>();
        times.add(firstEventTime);
        times.add(secondEventTime);
        times.add(thirdEventTime);
        scheduleEvent(times, request.get("id").toString());

    }
    public void scheduleEvent(List<Date>times, String id){
        Timer timer = new Timer();
        Date date1 = times.get(0);
        timer.schedule(new TimerTask() {
            public void run() {
                Checkout checkout = getCheckoutByCartIdAndNotPlaced(id);
                if (!checkout.getOrderPlaced()){
                    Message message = new Message();
                    message.setCartId(id);
                    message.setFirstMessage("This is first Remainder to your cart "+id);
                    messageRepo.save(message);
                }
            }
        }, date1);

        Date date2 = times.get(1);
        TimerTask task2 = new TimerTask() {
            public void run() {
                Checkout checkout = getCheckoutByCartIdAndNotPlaced(id);
                if (!checkout.getOrderPlaced()){
                    Message message = getMessageByCartId(id);

                    message.setSecondMessage("This is Second Remainder to your cart "+id);
                    messageRepo.save(message);
                }
            }
        };
        timer.schedule(task2, date2);


        Date date3 = times.get(2);
        TimerTask task3 = new TimerTask() {
            public void run() {
                Checkout checkout = getCheckoutByCartIdAndNotPlaced(id);
                if (!checkout.getOrderPlaced()){
                    Message message = getMessageByCartId(id);

                    message.setThirdMessage("This is Third Remainder to your cart "+id);
                    messageRepo.save(message);
                }

            }
        };
        timer.schedule(task3, date3);

    }

    public List<Checkout> getAllOrders() {
        return checkoutRepo.findAllByOrderPlacedIsTrue();
    }

    public List<Message> getAllMessage() {
        return messageRepo.findAll();
    }
}
