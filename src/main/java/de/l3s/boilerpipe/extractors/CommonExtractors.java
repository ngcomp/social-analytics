package de.l3s.boilerpipe.extractors;

/**
 * Provides quick access to common {@link de.l3s.boilerpipe.BoilerpipeExtractor}s.
 * 
 * @author Christian Kohlschütter
 */
public final class CommonExtractors {
	private CommonExtractors() {
	}

	/**
	 * Works very well for most types of Article-like HTML.
	 */
	public static final ArticleExtractor ARTICLE_EXTRACTOR = ArticleExtractor.INSTANCE;

	/**
	 * Usually worse than {@link de.l3s.boilerpipe.extractors.ArticleExtractor}, but simpler/no heuristics.
	 */
	public static final DefaultExtractor DEFAULT_EXTRACTOR = DefaultExtractor.INSTANCE;

	/**
	 * Like {@link de.l3s.boilerpipe.extractors.DefaultExtractor}, but keeps the largest text block only.
	 */
	public static final LargestContentExtractor LARGEST_CONTENT_EXTRACTOR = LargestContentExtractor.INSTANCE;
	
	
	/**
	 * Trained on krdwrd Canola (different definition of "boilerplate"). You may
	 * give it a try.
	 */
	public static final CanolaExtractor CANOLA_EXTRACTOR = CanolaExtractor.INSTANCE;

	/**
	 * Dummy Extractor; should return the input text. Use this to double-check
	 * that your problem is within a particular {@link de.l3s.boilerpipe.BoilerpipeExtractor}, or
	 * somewhere else.
	 */
	public static final KeepEverythingExtractor KEEP_EVERYTHING_EXTRACTOR = KeepEverythingExtractor.INSTANCE;
}
