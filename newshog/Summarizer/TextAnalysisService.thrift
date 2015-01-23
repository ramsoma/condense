//include "shared.thrift"

/**
 * Thrift files can namespace, package, or prefix their output in various
 * target languages.
 */
namespace cpp newshog.thrift
namespace d newshog.thrift
namespace java newshog.thrift
namespace php newshog.thrift
namespace perl newshog.thrift

struct TextAnalysisResult {
  1: string summary,
  2: list<string> wikiLinks,
  3: list<string> topTweets,
  4: list<string> topHashtags,
  5: string docSentiment,
  6: string twitterSentiment,	
}

struct Article{
  1: string text,
  2: string url,
  3: string date,
}

exception NewshogException {
  1: i32 what,
  2: string why
}

enum AnalysisOptions{
 All = 0,
 Summarization = 1,
 TextRank=2,
 LexChain=3,
 Wiki = 4,
 Twitter = 8,
 Sentiment = 16 
}

//extends shared.SharedService
service NewshogTextAnalysisService  {
   void ping(),
   TextAnalysisResult analyze(1:Article a, 2:AnalysisOptions options, int maxWords, int maxLinks) throws (1:NewshogException ne)
}
