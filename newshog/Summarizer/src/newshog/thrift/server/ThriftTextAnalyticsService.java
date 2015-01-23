package newshog.thrift.server;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;





// Generated code

import newshog.thrift.server.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ThriftTextAnalyticsService {

  public static NewshogTextAnalysisServiceHandler handler;

  public static NewshogTextAnalysisService.Processor processor;

  private static String thriftHome = System.getenv("THRIFT_HOME");//"/home/ram/summarizer/thrift/thrift-0.9.0";
  public static FileHandler fh ;

  static {
	  try {
		fh = new FileHandler("logs/summarizer.log", 10*1024*1024,3);
		fh.setFormatter(new SimpleFormatter());
	} catch (SecurityException e) {
		// TODO Auto-generated catch block
		Logger.getAnonymousLogger().severe("Exception occurred: " + e.getMessage());
	} catch (IOException e) {
		// TODO Auto-generated catch block
		Logger.getAnonymousLogger().severe("Exception occurred: " + e.getMessage());
	}
  }
  
  public static void main(String [] args) {
    try {
      Logger.getLogger("").addHandler(fh);
      
      handler = new NewshogTextAnalysisServiceHandler();
      processor = new NewshogTextAnalysisService.Processor(handler);

      Runnable simple = new Runnable() {
        public void run() {
          simple(processor);
        }
      };      

      Runnable secure = new Runnable() {
        public void run() {
          secure(processor);
        }
      };

      new Thread(simple).start();
//      new Thread(secure).start();
      Logger.getLogger(ThriftTextAnalyticsService.class.getName()).info("Started service, listening for requests");
    } catch (Exception x) {
		Logger.getAnonymousLogger().severe("Exception occurred: " + x.getMessage());
    }
  }

  public static void simple(NewshogTextAnalysisService.Processor processor) {
    try {
      TServerTransport serverTransport = new TServerSocket(9090);
      TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));

      // Use this for a multithreaded server
      // TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

      System.out.println("Started the simple server...");
      server.serve();
    } catch (Exception e) {
		Logger.getAnonymousLogger().severe("Exception occurred: " + e.getMessage());
    }
  }

  
  public static void secure(NewshogTextAnalysisService.Processor processor) {
    try {
      /*
       * Use TSSLTransportParameters to setup the required SSL parameters. In this example
       * we are setting the keystore and the keystore password. Other things like algorithms,
       * cipher suites, client auth etc can be set. 
       */
      TSSLTransportParameters params = new TSSLTransportParameters();
      // The Keystore contains the private key
      params.setKeyStore(thriftHome + "/lib/java/test/.keystore", "thrift", null, null);

      /*
       * Use any of the TSSLTransportFactory to get a server transport with the appropriate
       * SSL configuration. You can use the default settings if properties are set in the command line.
       * Ex: -Djavax.net.ssl.keyStore=.keystore and -Djavax.net.ssl.keyStorePassword=thrift
       * 
       * Note: You need not explicitly call open(). The underlying server socket is bound on return
       * from the factory class. 
       */
      TServerTransport serverTransport = TSSLTransportFactory.getServerSocket(9091, 0, null, params);
      TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));

      // Use this for a multi threaded server
      // TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

      System.out.println("Starting the secure server...");
      server.serve();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
}