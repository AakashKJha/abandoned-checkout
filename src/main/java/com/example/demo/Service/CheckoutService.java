package com.example.demo.Service;

import com.example.demo.Repo.CheckoutRepo;
import com.example.demo.Repo.MessageRepo;
import com.example.demo.Repo.RemainderRepo;
import com.example.demo.entity.Checkout;
import com.example.demo.entity.Message;
import com.example.demo.entity.TimeStamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class CheckoutService implements ApplicationRunner {

    @Autowired
    CheckoutRepo checkoutRepo;
    @Autowired
    MessageRepo messageRepo;
    PriorityQueue<TimeStamp> events =new PriorityQueue<TimeStamp>();

    @Autowired
    RemainderRepo remainderRepo;
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
            return null;
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
        Checkout savedCheckout= checkoutRepo.save(checkout);
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(calendar.MINUTE,first);
        Date firstEventTime = calendar.getTime();
        calendar.add(calendar.MINUTE,-first);
        calendar.add(calendar.MINUTE,second);
        Date secondEventTime = calendar.getTime();
        calendar.add(calendar.MINUTE,-second);
        calendar.add(calendar.MINUTE,third);
        Date thirdEventTime = calendar.getTime();
        ArrayList<Date>times = new ArrayList<>();
        times.add(firstEventTime);
        times.add(secondEventTime);
        times.add(thirdEventTime);
        int index =0;
        AtomicReference<Long> remainder= new AtomicReference<>(Long.valueOf(1));
        while (index < times.size()){
            TimeStamp timeStamp = new TimeStamp();
            timeStamp.setDate(times.get(index++));
            timeStamp.setCartId(savedCheckout.getCartId());
            timeStamp.setRemainder(remainder.getAndSet(remainder.get() + 1));
            events.add(timeStamp);
            remainderRepo.save(timeStamp);
        }


    }
    public void scheduleEvent(){
        Thread thread =new Thread(() -> {
            while (true) {

                // Check the condition
                if (!events.isEmpty()) {
                    if(events.peek().getDate().before(new Date())){
                        TimeStamp timeStamp = events.poll();
                    if(!getCheckoutByCartIdAndNotPlaced(timeStamp.getCartId()).getOrderPlaced()){
                        Message message;
                        if(getMessageByCartId(timeStamp.getCartId()) != null){
                            message = getMessageByCartId(timeStamp.getCartId());
                            message.setFirstMessage(message.getFirstMessage()+"This is your "+timeStamp.getRemainder()+"th remainder");
                        }
                        else{
                            message = new Message();
                            message.setFirstMessage("This is your first message");
                            message.setCartId(timeStamp.getCartId());
                        }

                        messageRepo.save(message);
                    }
                    deleteTime(timeStamp);
                    }


                }
                else {
                    System.out.println("Event is empty and size is: "+ events.size());
                    while (events.isEmpty()){
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });
        thread.start();

    }

    public List<Checkout> getAllOrders() {
        return checkoutRepo.findAllByOrderPlacedIsTrue();
    }
    public void deleteTime(TimeStamp timeStamp){
        remainderRepo.delete(timeStamp);
    }

    public List<Message> getAllMessage() {
        return messageRepo.findAll();
    }

    public PriorityQueue<TimeStamp> getAllRemainder(){

        List<TimeStamp> stampList = remainderRepo.findAll();
        return addTimeInQueue(stampList,new PriorityQueue<TimeStamp>());
    }
    public PriorityQueue<TimeStamp> addTimeInQueue(List<TimeStamp>timeStamps,
                                                   PriorityQueue<TimeStamp> minHeapTime ){
        timeStamps.forEach(minHeapTime:: add);
        return minHeapTime;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        events = getAllRemainder();
        scheduleEvent();
    }
}
