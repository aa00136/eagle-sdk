package com.lgh.handler.decode;

import com.lgh.model.command.Command;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class CommandDecoder extends ByteToMessageDecoder {
    private int HEARD_LENGTH = 12;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < HEARD_LENGTH) {
            //in.resetReaderIndex();
            return;
        }
        int requestId = in.readInt();
        short commandCode = in.readShort();
        byte responseCode = in.readByte();
        int bodyLength = in.readInt();
        byte extentionLength = in.readByte();

        System.out.println("requestId=" + requestId + " " + commandCode + " " + in.readableBytes());
        if (in.readableBytes() < bodyLength) {
            //in.resetReaderIndex();
            return;
        }
        String body = readCommandContent(in, bodyLength);
        String extention = readCommandContent(in, extentionLength);

        Command cmd = new Command();
        cmd.setRequestId(requestId);
        cmd.setCommandCode(commandCode);
        cmd.setResponseCode(responseCode);
        cmd.setBody(body);
        cmd.setExtention(extention);

        out.add(cmd);
    }

    private String readCommandContent(ByteBuf buffer, int len) throws UnsupportedEncodingException {
        byte[] body = new byte[len];
        System.out.println("ridx=" + buffer.readerIndex() + "`widx=" + buffer.writerIndex() + "`length=" + len);
        buffer.readBytes(body);
        return new String(body, "utf-8");
    }
}
