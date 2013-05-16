package org.openimaj.demos.sandbox.ml.linear.learner.stream;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mortbay.util.ajax.JSON;
import org.openimaj.demos.twitter.TwitterStreamingDataset;
import org.openimaj.tools.twitter.modes.filter.LanguageFilter;
import org.openimaj.tools.twitter.modes.preprocessing.LanguageDetectionMode;
import org.openimaj.tools.twitter.modes.preprocessing.StopwordMode;
import org.openimaj.tools.twitter.modes.preprocessing.TokeniseMode;
import org.openimaj.twitter.USMFStatus;
import org.openimaj.util.api.auth.DefaultTokenFactory;
import org.openimaj.util.api.auth.common.TwitterAPIToken;
import org.openimaj.util.concurrent.ArrayBlockingDroppingQueue;
import org.openimaj.util.function.ListFilter;
import org.openimaj.util.function.ListFunction;
import org.openimaj.util.pair.IndependentPair;
import org.openimaj.util.stream.Stream;

import twitter4j.Status;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.ServerAddress;

/**
 * @author Sina Samangooei (ss@ecs.soton.ac.uk)
 *
 */
public class FinancialStreamRecorder {
	static Logger logger = Logger.getLogger(FinancialStreamRecorder.class);
	/**
	 * @param args
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static void main(String[] args) throws MalformedURLException, IOException {

		// The financial stream
		RealTimeWindowFunction<Map<String,Double>> yahooWindow = new RealTimeWindowFunction<Map<String,Double>>(5000);
		Stream<List<Map<String, Double>>> yahooAveragePriceStream = new YahooFinanceStream("AAPL","GOOG","GE","GM","TWX")
		.transform(yahooWindow);

		// The Twitter Stream
		final ArrayBlockingDroppingQueue<Status> buffer = new ArrayBlockingDroppingQueue<Status>(1);
		final LanguageDetectionMode languageDetectionMode = new LanguageDetectionMode();
		final StopwordMode stopwordMode = new StopwordMode();
		final TokeniseMode tokeniseMode = new TokeniseMode();

		Stream<List<USMFStatus>> twitterUserWordCountStream = new TwitterStreamingDataset(
			DefaultTokenFactory.get(TwitterAPIToken.class),buffer
		)
		.transform(new RealTimeWindowFunction<Status>(5000))
		.map(new ListFunction<Status,USMFStatus>(new TwitterStatusAsUSMFStatus()))
		.map(new ListFunction<USMFStatus,USMFStatus>(new TwitterPreprocessingFunction(languageDetectionMode,tokeniseMode,stopwordMode)))
		.map(new ListFilter<USMFStatus>(new TwitterPredicateFunction(new LanguageFilter("en"))));

		List<ServerAddress> serverList = Arrays.asList(
			new ServerAddress("rumi",27017),
			new ServerAddress("hafez",27017)
		);
		StreamCombiner.combine(twitterUserWordCountStream,yahooAveragePriceStream)
		.forEach(
			new MongoDBOutputOp<
				IndependentPair<
					List<USMFStatus>,
					List<Map<String,Double>>
			>>
			(serverList) {

				@Override
				public String getCollectionName() {
					return "streamapi_yahoo";
				}

				@Override
				public DBObject asDBObject(IndependentPair<List<USMFStatus>,List<Map<String,Double>>> obj) {
					BasicDBObject dbobj = new BasicDBObject();
					List<USMFStatus> tweets = obj.firstObject();
					List<Object> dbtweets = new ArrayList<Object>();
					for (USMFStatus usmfStatus : tweets) {
						dbtweets.add(JSON.parse(usmfStatus.toJson()));
					}
					dbobj.append("tweets", dbtweets);
					dbobj.append("tickers", obj.secondObject());
					long timestamp = System.currentTimeMillis();
					dbobj.append("timestamp", timestamp);
					logger.debug(String.format("Dumping %d tweets and %d stock-ticks at %d",dbtweets.size(),obj.secondObject().size(),timestamp));
					return dbobj;
				}

				@Override
				public String getDBName() {
					return "twitterticker";
				}
			}
		);
	}
}