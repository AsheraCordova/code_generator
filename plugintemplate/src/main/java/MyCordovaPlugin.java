package @packagename@;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

public class Cordova@name@Plugin extends CordovaPlugin {
	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		@name@Plugin.initPlugin();
	}
}
