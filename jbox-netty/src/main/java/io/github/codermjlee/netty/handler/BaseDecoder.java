package io.github.codermjlee.netty.handler;

import io.github.codermjlee.common.util.binary.Bytes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MJ
 */
@Slf4j
public abstract class BaseDecoder extends ByteToMessageDecoder {
    private static final int MAX_BYTES = 10240;
    protected ByteBuf byteBuf;
    protected ChannelHandlerContext ctx;
    protected List<Object> out;
    protected List<Step> steps = new ArrayList<>();

    public BaseDecoder() {
        if (this instanceof Step) {
            steps.add((Step) this);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx,
                          ByteBuf byteBuf,
                          List<Object> out) throws Exception {
        this.byteBuf = byteBuf;
        this.ctx = ctx;
        this.out = out;

        prepare();

        // 防止socket字节流攻击
        int len = byteBuf.readableBytes();
        if (len >= MAX_BYTES || skip()) {
            // 直接丢弃这一段数据
            byteBuf.skipBytes(len);
            return;
        }

        // 解码步骤的数量
        int size = steps.size();

        // 挨个字节解析数据包
        while (lenValid(byteBuf.readableBytes())) {
            // 记录读取索引
            int idx = byteBuf.readerIndex();

            // 遍历所有的步骤
            for (int i = 0; i < size; i++) {
                Step step = steps.get(i);
                int bytes;
                try {
                    bytes = step.decode();
                } catch (Exception e) {
                    bytes = Step.NOT_FOUND;
                    log.error("异常", e);
                }

                // 解析到想要的数据
                if (bytes > 0) {
                    byteBuf.readerIndex(idx + bytes);
                    return;
                }

                // 恢复索引
                byteBuf.readerIndex(idx);

                // 找到了头部，需要更多数据
                if (bytes == Step.NEED_MORE) {
                    // 将它优先调度到数组的头部
                    if (i != 0) {
                        steps.set(i, steps.get(0));
                        steps.set(0, step);
                    }
                    return;
                }
            }

            // 略过一个字节
            byteBuf.readByte();
        }
    }
    protected void prepare() throws Exception {

    }

    /**
     * byteBuf的可读长度是否合理
     * @return true：合理，可以继续解析；false：不合理，直接退出整个decode过程
     */
    protected boolean lenValid(int len) throws Exception {
        return len > 0;
    }

    /**
     * 开头判断整个包是否不合理
     * @return true：不合理，直接丢弃整个包；false：合理，可以进入正常的解析流程
     */
    protected boolean skip() throws Exception {
        return false;
    }

    /**
     * 头部是否匹配
     */
    protected boolean headFound(byte[] head) {
        byte[] newHead = new byte[head.length];
        byteBuf.markReaderIndex();
        byteBuf.readBytes(newHead);
        byteBuf.resetReaderIndex();
        return Bytes.equals(newHead, head);
    }

    /**
     * byteBuf回退len长度
     */
    protected void back(int len) {
        byteBuf.readerIndex(byteBuf.readerIndex() - len);
    }

    /**
     * byteBuf前进len长度
     */
    protected void forward(int len) {
        byteBuf.readerIndex(byteBuf.readerIndex() + len);
    }

    @FunctionalInterface
    public interface Step {
        /** 没有找到想要的数据 */
        int NOT_FOUND = -1;
        /** 找到了头部，需要更多数据 */
        int NEED_MORE = 0;

        /**
         * @return 返回解码过程中读取的字节数
         */
        int decode() throws Exception;
    }
}
