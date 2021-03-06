package daily.y2016.m08.d18.netty.example.objectecho;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;

import org.jboss.netty.handler.codec.serialization.ClassResolver;

public class CompactObjectInputStream extends ObjectInputStream {

	private final ClassResolver classResolver;
	
	CompactObjectInputStream(InputStream in, ClassResolver classResolver) throws IOException {
		super(in);
		this.classResolver = classResolver;
	}
	
	@Override
	protected void readStreamHeader() throws IOException {
		int version = readByte() & 0xFF;
		if(version != STREAM_VERSION) {
			throw new StreamCorruptedException(
					"Unsupported version :" + version);
		}
	}
	
	@Override
	protected ObjectStreamClass readClassDescriptor() throws IOException , ClassNotFoundException {
		
		int type = read();
		if(type<0) {
			throw new EOFException ();
		}
		switch(type) {
		case CompactObjectOutputStream.TYPE_FAT_DESCRIPTOR:
			return super.readClassDescriptor();
		case CompactObjectOutputStream.TYPE_THIN_DESCRIPTOR:
			String className = readUTF();
			Class<?> clazz = classResolver.resolve(className);
			return ObjectStreamClass.lookupAny(clazz);
		default:
			throw new StreamCorruptedException(
					"Unexpected class description type:" + type);
		}
	}
	
	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
		Class<?> clazz;
		try {
			clazz = classResolver.resolve(desc.getName());
		} catch(ClassNotFoundException e) {
			clazz = super.resolveClass(desc) ;
		}
		
		return clazz;
	}
}
