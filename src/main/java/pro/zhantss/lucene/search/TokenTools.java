package pro.zhantss.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import pro.zhantss.lucene.data.Resources;

public class TokenTools {

	public static String[] complexTokens(String input) throws IOException {
		Analyzer analyzer = Resources.complex;
		return mmsegTokens(analyzer, input);
	}

	public static String[] maxWordTokens(String input) throws IOException {
		Analyzer analyzer = Resources.maxWord;
		return mmsegTokens(analyzer, input);
	}

	public static String[] simpleTokens(String input) throws IOException {
		Analyzer analyzer = Resources.simple;
		return mmsegTokens(analyzer, input);
	}

	private static String[] mmsegTokens(Analyzer analyzer, String input)
			throws IOException {
		if (Resources.debug) {
			Resources.LOGGER.debug("TokenParser:" + input);
			Resources.LOGGER.debug("Analyzer:" + analyzer.getClass());
		}
		TokenStream tokenStream = analyzer.tokenStream("input", input);
		OffsetAttribute offsetAttribute = tokenStream
				.addAttribute(OffsetAttribute.class);
		PositionIncrementAttribute positionIncrementAttribute = tokenStream
				.addAttribute(PositionIncrementAttribute.class);
		CharTermAttribute charTermAttribute = tokenStream
				.addAttribute(CharTermAttribute.class);
		TypeAttribute typeAttribute = tokenStream
				.addAttribute(TypeAttribute.class);
		tokenStream.reset();
		int position = 0;

		List<String> tokens = new ArrayList<String>();
		while (tokenStream.incrementToken()) {
			int increment = positionIncrementAttribute.getPositionIncrement();
			if (increment > 0) {
				position = position + increment;
				if (Resources.debug) {
					Resources.LOGGER.debug(position + ":");
				}
			}

			int startOffset = offsetAttribute.startOffset();
			int endOffset = offsetAttribute.endOffset();
			String term = charTermAttribute.toString();
			tokens.add(term);
			if (Resources.debug) {
				Resources.LOGGER.debug("[" + term + "]" + ":(" + startOffset
						+ "-->" + endOffset + "):" + typeAttribute.type());
			}
		}
		tokenStream.end();
		tokenStream.close();
		return tokens.toArray(new String[]{});
	}
	
	public static String[] complexGroup(String input) throws IOException {
		Analyzer analyzer = Resources.complex;
		return groupTokens(analyzer, input);
	}

	public static String[] maxWordGroup(String input) throws IOException {
		Analyzer analyzer = Resources.maxWord;
		return groupTokens(analyzer, input);
	}

	public static String[] simpleGroup(String input) throws IOException {
		Analyzer analyzer = Resources.simple;
		return groupTokens(analyzer, input);
	}

	private static String[] groupTokens(Analyzer analyzer, String input) throws IOException {
		if (Resources.debug) {
			Resources.LOGGER.debug("TokenParser:" + input);
			Resources.LOGGER.debug("Analyzer:" + analyzer.getClass());
		}
		TokenStream tokenStream = analyzer.tokenStream("input", input);
		OffsetAttribute offsetAttribute = tokenStream
				.addAttribute(OffsetAttribute.class);
		PositionIncrementAttribute positionIncrementAttribute = tokenStream
				.addAttribute(PositionIncrementAttribute.class);
		CharTermAttribute charTermAttribute = tokenStream
				.addAttribute(CharTermAttribute.class);
		TypeAttribute typeAttribute = tokenStream
				.addAttribute(TypeAttribute.class);
		tokenStream.reset();
		int position = 0;

		List<TermInfo> infos = new ArrayList<TermInfo>();
		while (tokenStream.incrementToken()) {
			int increment = positionIncrementAttribute.getPositionIncrement();
			if (increment > 0) {
				position = position + increment;
				if (Resources.debug) {
					Resources.LOGGER.debug(position + ":");
				}
			}

			int startOffset = offsetAttribute.startOffset();
			int endOffset = offsetAttribute.endOffset();
			String term = charTermAttribute.toString();
			TermInfo info = new TermInfo();
			info.setStart(startOffset);
			info.setEnd(endOffset);
			infos.add(info);
			if (Resources.debug) {
				Resources.LOGGER.debug("[" + term + "]" + ":(" + startOffset
						+ "-->" + endOffset + "):" + typeAttribute.type());
			}
		}
		tokenStream.end();
		tokenStream.close();
		
		Stack<TermInfo> tiStack = groupTokenInfos(infos);
		List<String> terms = new ArrayList<String>();
		while (!tiStack.isEmpty()) {
			TermInfo termInfo = tiStack.pop();
			if (termInfo.getEnd() <= input.length() && termInfo.getStart() >= 1) {
				String term = input.substring(termInfo.getStart(), termInfo.getEnd());
				terms.add(term);
			}
		}
		return terms.toArray(new String[]{});
	}
	
	private static Stack<TermInfo> groupTokenInfos(List<TermInfo> infos) {
		List<TermInfo> tis = new ArrayList<TermInfo>();
		for (TermInfo info : infos) {
			Integer len = info.getEnd()- info.getStart();
			if (len == 1) {
				tis.add(info);
			}
		}
	
		Stack<TermInfo> tiStack = new Stack<TermInfo>();
		for (Integer index = 0; index < tis.size(); index++) {
			TermInfo info = tis.get(index);
			TermInfo plus = info;
			for (Integer next = index + 1; next < tis.size(); next++) {
				TermInfo nextInfo = tis.get(next);
				if (plus == null) {
					plus = nextInfo;
					continue;
				}
				plus = plus.plus(nextInfo);
				if (plus != null) {
					if (tiStack.isEmpty()) {
						tiStack.push(plus);
					} else if (!tiStack.contains(plus)) {
						tiStack.push(plus);
					}
				}
			}
		}
		return tiStack;
	}

}
