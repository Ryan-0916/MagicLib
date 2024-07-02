package com.magicrealms.magiclib.common.utils;

import java.io.Serial;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ID 生成器
 */
@SuppressWarnings("unused")
public final class IdGeneratorUtil implements Serializable {
    @Serial
    private static final long serialVersionUID = 3670079982654483072L;
    private static final int MACHINE_IDENTIFIER;
    private static final short PROCESS_IDENTIFIER;
    private static final AtomicInteger NEXT_COUNTER = new AtomicInteger((new SecureRandom()).nextInt());
    private static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private final int timestamp;
    private final int machineIdentifier;
    private final short processIdentifier;
    private final int counter;

    public static String getId(){
        return new IdGeneratorUtil().toString();
    }

    public IdGeneratorUtil() {
        this(new Date());
    }

    public IdGeneratorUtil(Date date) {
        this(dateToTimestampSeconds(date), MACHINE_IDENTIFIER, PROCESS_IDENTIFIER, NEXT_COUNTER.getAndIncrement(), false);
    }

    private IdGeneratorUtil(int timestamp, int machineIdentifier, short processIdentifier, int counter, boolean checkCounter) {
        if ((machineIdentifier & -16777216) != 0) {
            throw new IllegalArgumentException("The machine identifier must be between 0 and 16777215 (it must fit in three bytes).");
        } else if (checkCounter && (counter & -16777216) != 0) {
            throw new IllegalArgumentException("The counter must be between 0 and 16777215 (it must fit in three bytes).");
        } else {
            this.timestamp = timestamp;
            this.machineIdentifier = machineIdentifier;
            this.processIdentifier = processIdentifier;
            this.counter = counter & 16777215;
        }
    }

    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(12);
        this.putToByteBuffer(buffer);
        return buffer.array();
    }

    public void putToByteBuffer(ByteBuffer buffer) {
        buffer.put(int3(this.timestamp));
        buffer.put(int2(this.timestamp));
        buffer.put(int1(this.timestamp));
        buffer.put(int0(this.timestamp));
        buffer.put(int2(this.machineIdentifier));
        buffer.put(int1(this.machineIdentifier));
        buffer.put(int0(this.machineIdentifier));
        buffer.put(short1(this.processIdentifier));
        buffer.put(short0(this.processIdentifier));
        buffer.put(int2(this.counter));
        buffer.put(int1(this.counter));
        buffer.put(int0(this.counter));
    }

    public String toHexString() {
        char[] chars = new char[24];
        int i = 0;
        byte[] var3 = this.toByteArray();

        for (byte b : var3) {
            chars[i++] = HEX_CHARS[b >> 4 & 15];
            chars[i++] = HEX_CHARS[b & 15];
        }

        return new String(chars);
    }



    public int hashCode() {
        int result = this.timestamp;
        result = 31 * result + this.machineIdentifier;
        result = 31 * result + this.processIdentifier;
        result = 31 * result + this.counter;
        return result;
    }



    public String toString() {
        return this.toHexString();
    }

    /** @deprecated */
    @Deprecated
    public int getTimeSecond() {
        return this.timestamp;
    }

    /** @deprecated */
    @Deprecated
    public long getTime() {
        return (long)this.timestamp * 1000L;
    }

    /** @deprecated */
    @Deprecated
    public String toStringMongod() {
        return this.toHexString();
    }

    private static int createMachineIdentifier() {
        int machinePiece;
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();

            while(e.hasMoreElements()) {
                NetworkInterface ni = e.nextElement();
                sb.append(ni.toString());
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    ByteBuffer bb = ByteBuffer.wrap(mac);

                    try {
                        sb.append(bb.getChar());
                        sb.append(bb.getChar());
                        sb.append(bb.getChar());
                    } catch (BufferUnderflowException ignored) {
                    }
                }
            }

            machinePiece = sb.toString().hashCode();
        } catch (Throwable var8) {
            machinePiece = (new SecureRandom()).nextInt();
        }

        machinePiece &= 16777215;
        return machinePiece;
    }

    private static short createProcessIdentifier() {
        short processId;
        try {
            String processName = ManagementFactory.getRuntimeMXBean().getName();
            if (processName.contains("@")) {
                processId = (short)Integer.parseInt(processName.substring(0, processName.indexOf(64)));
            } else {
                processId = (short)ManagementFactory.getRuntimeMXBean().getName().hashCode();
            }
        } catch (Throwable var2) {
            processId = (short)(new SecureRandom()).nextInt();
        }

        return processId;
    }

    private static int dateToTimestampSeconds(Date time) {
        return (int)(time.getTime() / 1000L);
    }

    private static byte int3(int x) {
        return (byte)(x >> 24);
    }

    private static byte int2(int x) {
        return (byte)(x >> 16);
    }

    private static byte int1(int x) {
        return (byte)(x >> 8);
    }

    private static byte int0(int x) {
        return (byte)x;
    }

    private static byte short1(short x) {
        return (byte)(x >> 8);
    }

    private static byte short0(short x) {
        return (byte)x;
    }

    static {
        try {
            MACHINE_IDENTIFIER = createMachineIdentifier();
            PROCESS_IDENTIFIER = createProcessIdentifier();
        } catch (Exception var1) {
            throw new RuntimeException(var1);
        }
    }
}
