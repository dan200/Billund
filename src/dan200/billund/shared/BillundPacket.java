/**
 * This file is part of Billund - http://www.computercraft.info/billund
 * Copyright Daniel Ratcliffe, 2013-2014. See LICENSE for license details.
 */

package dan200.billund.shared;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
 
public class BillundPacket
{
	// Static Packet types
	public static final byte OrderSet = 1;
	
	// Packet class
	public byte packetType;
	public String[] dataString;
	public int[] dataInt;
	
	public BillundPacket()
	{
		packetType = 0;
		dataString = null;
		dataInt = null;
	}
	
	private void writeData( DataOutputStream data ) throws IOException 
	{
		data.writeByte(packetType);
		if( dataString != null ) {
			data.writeByte(dataString.length);
		} else {
			data.writeByte(0);
		}
		if( dataInt != null ) {
			data.writeByte(dataInt.length);
		} else {
			data.writeByte(0);
		}
		if( dataString != null ) {
			for(String s : dataString) {
				data.writeUTF(s);
			}
		}
		if( dataInt != null ) {
			for(int i : dataInt) {
				data.writeInt(i);
			}
		}
	}

	private void readData( DataInputStream data ) throws IOException
	{
		packetType = data.readByte();
		byte nString = data.readByte();
		byte nInt = data.readByte();
		if(nString > 128 || nInt > 128 || nString < 0 || nInt < 0 ) {
			throw new IOException("");
		}
		if(nString == 0) {
			dataString = null;
 		} else {
			dataString = new String[nString];
			for(int k = 0; k < nString; k++) {
				dataString[k] = data.readUTF();
			}
		}
		if(nInt == 0) {
			dataInt = null;
		} else {
			dataInt = new int[nInt];
			for(int k = 0; k < nInt; k++) {
				dataInt[k] = data.readInt();
			}
		}
	}

    /* This writes the P230's contents into the byte array of a real packet, on the given channel */
	public Packet toPacket()
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(bytes);
        Packet250CustomPayload pkt = new Packet250CustomPayload();
        try
        {
            writeData(data);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        pkt.channel = "Billund";
        pkt.data    = bytes.toByteArray();
        pkt.length  = pkt.data.length;
        return pkt;
    }

    /* This reads a byte array into a new P230 */
	public static BillundPacket parse( byte[] bytes ) throws IOException
	{
		DataInputStream data = new DataInputStream(new ByteArrayInputStream(bytes));
		BillundPacket pkt = new BillundPacket();
		pkt.readData( data );
		return pkt;
	}
}
