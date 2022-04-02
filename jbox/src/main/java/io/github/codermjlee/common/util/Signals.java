package io.github.codermjlee.common.util;

import lombok.ToString;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 信号解耦
 *
 * @author MJ
 */
@ToString
public class Signals {
    private static final List<Signals> SIGNALS = new LinkedList<>();
    private Object key;
    private Object value;
    private Consumer<Signals> receiver;
    // 小于等于0代表永远接收，除非主动删除
    private int receiveTimes;
    // 有效期
    private Long expireTime;
    private Object sender;

    private Signals() {}

    public static Signals alloc() {
        return new Signals();
    }

    public Object getValue() {
        return value;
    }

    public Signals receiveTimes(int receiveTimes) {
        this.receiveTimes = receiveTimes;
        return this;
    }

    public Signals expireTime(long expireTime) {
        this.expireTime = expireTime;
        return this;
    }

    public Signals duration(long duration) {
        return expireTime(System.currentTimeMillis() + duration);
    }

    public Signals key(Object key) {
        this.key = key;
        return this;
    }

    public Signals value(Object value) {
        this.value = value;
        return this;
    }

    public Signals receiver(Consumer<Signals> receiver) {
        this.receiver = receiver;
        return this;
    }

    public Signals sender(Object sender) {
        this.sender = sender;
        return this;
    }

    public void add() {
        if (receiver == null) return;

        synchronized (SIGNALS) {
            SIGNALS.add(this);
        }
    }

    public void remove() {
        synchronized (SIGNALS) {
            Iterator<Signals> it = SIGNALS.iterator();
            while (it.hasNext()) {
                Signals signals = it.next();

                // key对不上
                if ((key != null) && !key.equals(signals.key)) continue;
                // sender对不上
                if ((sender != null) && !sender.equals(signals.sender)) continue;
                // sender对不上
                if ((receiver != null) && !receiver.equals(signals.receiver)) continue;

                // 移除
                it.remove();
            }
        }
    }

    public void send() {
        synchronized (SIGNALS) {
            Iterator<Signals> it = SIGNALS.iterator();
            while (it.hasNext()) {
                Signals signals = it.next();
                if (signals.receiver == null) continue;

                // 时间
                if (signals.expireTime != null && System.currentTimeMillis() > signals.expireTime) {
                    // 过期了
                    it.remove();
                    continue;
                }

                // key对不上
                if ((signals.key != null) && !signals.key.equals(key)) continue;
                // sender对不上
                if ((signals.sender != null) && !signals.sender.equals(sender)) continue;

                // 发送信号
                Signals send = new Signals();
                send.key = key;
                send.sender = sender;
                send.value = value;
                signals.receiver.accept(send);

                // 次数
                if (signals.receiveTimes == 1) { // 只接收1次
                    it.remove();
                } else if (signals.receiveTimes > 1) {
                    signals.receiveTimes--;
                }
            }
        }
    }

//    public static void main(String[] args) {
//        Consumer<Signals> fn = System.out::println;
//        Signals.alloc().key("add").receiver(fn).add();
//        Signals.alloc().key("remove").receiver(fn).add();
//
//        Signals.alloc().key("add").value("1").sender("Jack").send();
//        Signals.alloc().key("add").value("2").send();
//        Signals.alloc().key("add").value("3").sender("Rose").send();
//
//        Signals.alloc().key("remove").value("4").sender("Jack").send();
//        Signals.alloc().key("remove").value("5").send();
//        Signals.alloc().key("remove").value("6").sender("Rose").send();
//    }
}
