package com.vdurmont.emoji;

import java.util.ArrayList;
import java.util.Collection;

import com.vdurmont.emoji.EmojiParser.FitzpatrickAction;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String str = "An :grinning:awesome :relaxed:string &#128516;with a few :wink:emojis!";
		String result = EmojiParser.parseToUnicode(str);
		System.out.println(result);

		
		String str2 = "An 😀awesome ☺string with a few 😉emojis!";
		String result2 = EmojiParser.parseToAliases(str2);
		System.out.println(result2);
		
		String str3 = "Here is a boy: \uD83D\uDC66\uD83C\uDFFF!";
		System.out.println(EmojiParser.parseToAliases(str3));
		System.out.println(EmojiParser.parseToAliases(str3, FitzpatrickAction.PARSE));
		// Prints twice: "Here is a boy: :boy|type_6:!"
		System.out.println(EmojiParser.parseToAliases(str3, FitzpatrickAction.REMOVE));
		// Prints: "Here is a boy: :boy:!"
		System.out.println(EmojiParser.parseToAliases(str3, FitzpatrickAction.IGNORE));
		// Prints: "Here is a boy: :boy:🏿!"
		
		
		String str4 = "An 😀awesome 😃string with a few 😉emojis!";
		String resultDecimal = EmojiParser.parseToHtmlDecimal(str4);
		System.out.println(resultDecimal);
		// Prints:
		// "An &#128512;awesome &#128515;string with a few &#128521;emojis!"

		String resultHexadecimal = EmojiParser.parseToHtmlHexadecimal(str4);
		System.out.println(resultHexadecimal);
		// Prints:
		// "An &#x1f600;awesome &#x1f603;string with a few &#x1f609;emojis!"
		
		
		String str5 = "Here is a boy: \uD83D\uDC66\uD83C\uDFFF!";
		System.out.println(EmojiParser.parseToHtmlDecimal(str5));
		System.out.println(EmojiParser.parseToHtmlDecimal(str5, FitzpatrickAction.PARSE));
		System.out.println(EmojiParser.parseToHtmlDecimal(str5, FitzpatrickAction.REMOVE));
		// Print 3 times: "Here is a boy: &#128102;!"
		System.out.println(EmojiParser.parseToHtmlDecimal(str5, FitzpatrickAction.IGNORE));
		// Prints: "Here is a boy: &#128102;🏿!"
		
		
		
		String str6 = "An 😀awesome 😃string with a few 😉emojis!";
		Collection<Emoji> collection = new ArrayList<Emoji>();
		collection.add(EmojiManager.getForAlias("wink")); // This is 😉

		System.out.println(EmojiParser.removeAllEmojis(str6));
		System.out.println(EmojiParser.removeAllEmojisExcept(str6, collection));
		System.out.println(EmojiParser.removeEmojis(str6, collection));

		// Prints:
		// "An awesome string with a few emojis!"
		// "An awesome string with a few 😉emojis!"
		// "An 😀awesome 😃string with a few emojis!"
	}

}
