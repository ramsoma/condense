package unittests;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

import newshog.Sentence;
import newshog.preprocess.DocProcessor;

import org.junit.BeforeClass;
import org.junit.Test;

public class DocProcessorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void test() {
		DocProcessor dp = new DocProcessor();
		boolean val;
		/*try {
			val = dp.isScript("== -1)  (loopCtr \"' '); commentText = commentText.replace(\"\"' '); loopCtr++; } "
					+ "//console.log(commentText); $(this).find(\".fyre-comment\").css('display''none'); "
					+ "$(this).find(\".fyre-comment-head\").append(commentText); } // end check for blank text } "
					+ "//console.log(\"Checking Height\"); cntHeight = $(this).find(\".fyre-comment-head\").height(); "
					+ "if (cntHeight  0){ //console.log(\"Setting Height\"); cntHeight = -1 * ((cntHeight / 2) + 12); "
					+ "$(this).find(\".fyre-flag-link\").css('top'cntHeight); } }); } function change_profile_link(){ "
					+ "$(\".fyre .fyre-box-list .fyre-edit-profile-link a\").attr(\"href\"'#'); "
					+ "$(\".fyre .fyre-box-list .fyre-edit-profile-link a\").attr(\"alt\"''); $('.fyre .fyre-box-list "
					+ ".fyre-edit-profile-link a').click(function() { return false; }); $('.fyre .fyre-box-list "
					+ ".fyre-edit-profile-link').remove(); $(\".fyre .fyre-comment-head .fyre-comment-username\").attr(\"href\"'#'); "
					+ "$(\".fyre .fyre-comment-head .fyre-comment-username\").attr(\"target\"''); "
					+ "$(\".fyre .fyre-comment-head .fyre-comment-username\").attr(\"alt\"''); "
					+ "$('.fyre .fyre-comment-head .fyre-comment-username').click(function() { return false; });");

			assertEquals(true, val);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}// $('.fyre .fyre-comment-head').each(function() { var alltxt = $(this).html(); var nickname = $(this).find('.fyre-comment-username').html(); if (!.. /gi''); $(this).html(alltxt); } }); } function open_profile(profile_link_url){ window.open(profile_link_url); } $(document).ready( function () { // Log in the user if we got a token for them // Change the DOM after login since things get re-rendered //console.log("== LF Calling Load =="); var conv = fyre.conv.load({"network" "cbssports.fyre.co" 'strings' customStrings authDelegate authDelegate} lf_config function(widget) { //console.log("== LF in Load =="); widget.on('initialRenderComplete' function () { //console.log("== LF Render Complete =="); $('#lf_comments_label').show(); loggedin = readCookie('pid'); //CBSi.log(loggedin); if ((typeof loggedin == "string")  (loggedin.match(/^L/))) { isLoggedIn = 1; if (typeof userObj.token !");
		
		boolean val2;
		try {
			val2 = dp.isScript("This is a test line..");
			assertEquals(val2, false);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	//	assertEquals(val2, true);
		String fileName="/Users/ram/dev/summarizer/test/forram/technology/input/9.txt";
		List<Sentence> doc = dp.docToSentList(fileName);//dp.docToString(fileName);//
		System.out.println(doc.size());
		for(int i=0;i<doc.size();i++)
			System.out.println(doc.get(i));
	}

}
