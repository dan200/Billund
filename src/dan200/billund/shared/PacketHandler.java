
package dan200.billund.shared;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import dan200.Billund;

public class PacketHandler implements IPacketHandler 
{
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload _packet, Player player) 
	{
		try
		{
			BillundPacket packet = BillundPacket.parse( _packet.data );
			Billund.handlePacket( packet, player );
		}
		catch( Exception e )
		{
			// Something failed, ignore it
			//e.printStackTrace();
		}	
	}
}
