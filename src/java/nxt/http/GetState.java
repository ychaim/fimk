package nxt.http;

import nxt.Account;
import nxt.Alias;
import nxt.Asset;
import nxt.AssetTransfer;
import nxt.Generator;
import nxt.Nxt;
import nxt.Order;
import nxt.Trade;
import nxt.Currency;
import nxt.Exchange;
import nxt.CurrencyTransfer;

import nxt.db.DbIterator;
import nxt.peer.Peer;
import nxt.peer.Peers;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import javax.servlet.http.HttpServletRequest;

public final class GetState extends APIServlet.APIRequestHandler {

    static final GetState instance = new GetState();

    private GetState() {
        super(new APITag[] {APITag.INFO});
    }

    @Override
    JSONStreamAware processRequest(HttpServletRequest req) {

        JSONObject response = new JSONObject();

        response.put("application", Nxt.APPLICATION);
        response.put("version", Nxt.VERSION);
        response.put("time", Nxt.getEpochTime());
        response.put("lastBlock", Nxt.getBlockchain().getLastBlock().getStringId());
        response.put("cumulativeDifficulty", Nxt.getBlockchain().getLastBlock().getCumulativeDifficulty().toString());

        long totalEffectiveBalance = 0;
        try (DbIterator<Account> accounts = Account.getAllAccounts(0, -1)) {
            for (Account account : accounts) {
                long effectiveBalanceNXT = account.getEffectiveBalanceNXT();
                if (effectiveBalanceNXT > 0) {
                    totalEffectiveBalance += effectiveBalanceNXT;
                }
            }
        }
        response.put("totalEffectiveBalanceNXT", totalEffectiveBalance);

        response.put("numberOfBlocks", Nxt.getBlockchain().getHeight() + 1);
        response.put("numberOfTransactions", Nxt.getBlockchain().getTransactionCount());
        response.put("numberOfAccounts", Account.getCount());
        response.put("numberOfAssets", Asset.getCount());
        response.put("numberOfOrders", Order.Ask.getCount() + Order.Bid.getCount());
        response.put("numberOfTrades", Trade.getCount());
        response.put("numberOfTransfers", AssetTransfer.getCount());
        response.put("numberOfCurrencies", Currency.getCount());
        response.put("numberOfOffers", nxt.CurrencyBuy.getCount());
        response.put("numberOfExchanges", Exchange.getCount());
        response.put("numberOfCurrencyTransfers", CurrencyTransfer.getCount());
        response.put("numberOfAliases", Alias.getCount());
        //response.put("numberOfPolls", Poll.getCount());
        //response.put("numberOfVotes", Vote.getCount());
        response.put("numberOfPeers", Peers.getAllPeers().size());
        response.put("numberOfUnlockedAccounts", Generator.getAllGenerators().size());
        Peer lastBlockchainFeeder = Nxt.getBlockchainProcessor().getLastBlockchainFeeder();
        response.put("lastBlockchainFeeder", lastBlockchainFeeder == null ? null : lastBlockchainFeeder.getAnnouncedAddress());
        response.put("lastBlockchainFeederHeight", Nxt.getBlockchainProcessor().getLastBlockchainFeederHeight());
        response.put("isScanning", Nxt.getBlockchainProcessor().isScanning());
        response.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        response.put("maxMemory", Runtime.getRuntime().maxMemory());
        response.put("totalMemory", Runtime.getRuntime().totalMemory());
        response.put("freeMemory", Runtime.getRuntime().freeMemory());

        return response;
    }

}
