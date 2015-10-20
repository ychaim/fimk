package nxt;

import java.nio.ByteBuffer;

import nxt.Attachment.AbstractAttachment;
import nxt.util.Convert;

import org.json.simple.JSONObject;


public class MofoAttachment {

    public final static class NamespacedAliasAssignmentAttachment extends AbstractAttachment {
  
        private final String aliasName;
        private final String aliasURI;
    
        NamespacedAliasAssignmentAttachment(ByteBuffer buffer, byte transactionVersion) throws NxtException.NotValidException {
            super(buffer, transactionVersion);
            aliasName = Convert.readString(buffer, buffer.get(), Constants.MAX_ALIAS_LENGTH).trim().intern();
            aliasURI = Convert.readString(buffer, buffer.getShort(), Constants.MAX_ALIAS_URI_LENGTH).trim().intern();
        }
    
        NamespacedAliasAssignmentAttachment(JSONObject attachmentData) {
            super(attachmentData);
            aliasName = (Convert.nullToEmpty((String) attachmentData.get("alias"))).trim().intern();
            aliasURI = (Convert.nullToEmpty((String) attachmentData.get("uri"))).trim().intern();
        }
    
        public NamespacedAliasAssignmentAttachment(String aliasName, String aliasURI) {
            super();
            this.aliasName = aliasName.trim().intern();
            this.aliasURI = aliasURI.trim().intern();
        }
    
        @Override
        int getMySize() {
            return 1 + Convert.toBytes(aliasName).length + 2 + Convert.toBytes(aliasURI).length;
        }
    
        @Override
        void putMyBytes(ByteBuffer buffer) {
            byte[] alias = Convert.toBytes(this.aliasName);
            byte[] uri = Convert.toBytes(this.aliasURI);
            buffer.put((byte)alias.length);
            buffer.put(alias);
            buffer.putShort((short) uri.length);
            buffer.put(uri);
        }
    
        @SuppressWarnings("unchecked")
        @Override
        void putMyJSON(JSONObject attachment) {
            attachment.put("alias", aliasName);
            attachment.put("uri", aliasURI);
        }
    
        @Override
        public TransactionType getTransactionType() {
            return MofoTransactions.NamespacedAliasAssignmentTransaction.NAMESPACED_ALIAS_ASSIGNMENT;
        }
    
        public String getAliasName() {
            return aliasName;
        }
    
        public String getAliasURI() {
            return aliasURI;
        }
    }
  
    static abstract class PrivateAssetAllowedAttachment extends AbstractAttachment {
      
        private final long assetId;
    
        PrivateAssetAllowedAttachment(ByteBuffer buffer, byte transactionVersion) throws NxtException.NotValidException {
            super(buffer, transactionVersion);
            assetId = buffer.getLong();
        }
    
        PrivateAssetAllowedAttachment(JSONObject attachmentData) {
            super(attachmentData);
            this.assetId = Convert.parseUnsignedLong((String) attachmentData.get("asset"));
        }
    
        public PrivateAssetAllowedAttachment(long assetId) {
            super();
            this.assetId = assetId;
        }

        @Override
        int getMySize() {
            return 8;
        }
    
        @Override
        void putMyBytes(ByteBuffer buffer) {
            buffer.putLong(assetId);
        }
    
        @Override
        void putMyJSON(JSONObject attachment) {
            Asset.putAsset(attachment, assetId);
        }
        
        public long getAssetId() {
            return assetId;
        }
    }
    
    public final static class AddPrivateAssetAccountAttachment extends PrivateAssetAllowedAttachment {
    
        AddPrivateAssetAccountAttachment(ByteBuffer buffer, byte transactionVersion) throws NxtException.NotValidException {
            super(buffer, transactionVersion);
        }
    
        AddPrivateAssetAccountAttachment(JSONObject attachmentData) {
            super(attachmentData);
        }      
      
        public AddPrivateAssetAccountAttachment(long assetId) {
            super(assetId);
        }

        @Override
        public TransactionType getTransactionType() {
            return MofoTransactions.PrivateAssetAddAccountTransaction.PRIVATE_ASSET_ADD_ACCOUNT;
        }
        
    }
    
    public final static class RemovePrivateAssetAccountAttachment extends PrivateAssetAllowedAttachment {
        
      RemovePrivateAssetAccountAttachment(ByteBuffer buffer, byte transactionVersion) throws NxtException.NotValidException {
            super(buffer, transactionVersion);
        }

      RemovePrivateAssetAccountAttachment(JSONObject attachmentData) {
            super(attachmentData);
        }
    
        public RemovePrivateAssetAccountAttachment(long assetId) {
            super(assetId);
        }

        @Override
        public TransactionType getTransactionType() {
            return MofoTransactions.PrivateAssetRemoveAccountTransaction.PRIVATE_ASSET_REMOVE_ACCOUNT;
        }
    }

    public final static class PrivateAssetSetFeeAttachment extends AbstractAttachment {
        
        private final long assetId;
        private final int orderFeePercentage;
        private final int tradeFeePercentage;
    
        PrivateAssetSetFeeAttachment(ByteBuffer buffer, byte transactionVersion) throws NxtException.NotValidException {
            super(buffer, transactionVersion);
            this.assetId = buffer.getLong();
            this.orderFeePercentage = buffer.getInt();
            this.tradeFeePercentage = buffer.getInt();
        }
    
        PrivateAssetSetFeeAttachment(JSONObject attachmentData) {
            super(attachmentData);
            this.assetId = Convert.parseUnsignedLong((String) attachmentData.get("asset"));
            this.orderFeePercentage = ((Long)attachmentData.get("orderFeePercentage")).intValue();
            this.tradeFeePercentage = ((Long)attachmentData.get("tradeFeePercentage")).intValue();
        }
    
        public PrivateAssetSetFeeAttachment(long assetId, int orderFeePercentage, int tradeFeePercentage) {
            super();
            this.assetId = assetId;
            this.orderFeePercentage = orderFeePercentage;
            this.tradeFeePercentage = tradeFeePercentage;
        }

        @Override
        int getMySize() {
            return 8 + 4 + 4;
        }
    
        @Override
        void putMyBytes(ByteBuffer buffer) {
            buffer.putLong(assetId);
            buffer.putInt(orderFeePercentage);
            buffer.putInt(tradeFeePercentage);
        }
    
        @SuppressWarnings("unchecked")
        @Override
        void putMyJSON(JSONObject attachment) {
            Asset.putAsset(attachment, assetId);
            attachment.put("orderFeePercentage", orderFeePercentage);
            attachment.put("tradeFeePercentage", tradeFeePercentage);
        }
    
        @Override
        public TransactionType getTransactionType() {
            return MofoTransactions.PrivateAssetSetFeeTransaction.PRIVATE_ASSET_SET_FEE;
        }

        public long getAssetId() {
            return assetId;
        }

        public int getOrderFeePercentage() {
            return orderFeePercentage;
        }

        public int getTradeFeePercentage() {
            return tradeFeePercentage;
        }
    }

    public final static class AccountIdAssignmentAttachment extends AbstractAttachment {

        private final String identifier;
        private final long signatory;
        private final byte[] signature;

        AccountIdAssignmentAttachment(ByteBuffer buffer, byte transactionVersion) throws NxtException.NotValidException {
            super(buffer, transactionVersion);
            this.identifier = Convert.readString(buffer, buffer.get(), Constants.MAX_ACCOUNT_IDENTIFIER_LENGTH).trim().intern();
            this.signatory = buffer.getLong();            
            int signatureLength = buffer.get();
            if (signatureLength != 64) {
                signatureLength = 0;
            }
            if (signatureLength == 0) {
                this.signature = null;
            }
            else {
                this.signature = new byte[signatureLength];
                buffer.get(this.signature);
            }
        }

        AccountIdAssignmentAttachment(JSONObject attachmentData) {
            super(attachmentData);
            this.identifier = (Convert.nullToEmpty((String) attachmentData.get("identifier"))).trim().intern();
            this.signatory = Convert.parseUnsignedLong((String) attachmentData.get("signatory"));
            this.signature = Convert.parseHexString((String) attachmentData.get("signature"));
        }
        
        public AccountIdAssignmentAttachment(String identifier, long signatory, byte[] signature) {
            this.identifier = identifier.trim();
            this.signatory = signatory;
            this.signature = signature;
        }

        @Override
        int getMySize() {
            return 1 + 8 + Convert.toBytes(identifier).length + 2 + (signature != null ? signature.length : 0);
        }

        @Override
        void putMyBytes(ByteBuffer buffer) {
            byte[] identifier = Convert.toBytes(this.identifier);
            buffer.put((byte)identifier.length);
            buffer.put(identifier);
            buffer.putLong(signatory);
            buffer.put(signature != null ? (byte)signature.length : (byte)0);
            if (signature != null) {
              buffer.put(signature);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        void putMyJSON(JSONObject attachment) {
            attachment.put("identifier", identifier);
            attachment.put("signatory", Long.toUnsignedString(signatory));
            if (signature != null) {
              attachment.put("signature", Convert.toHexString(signature));
            }
        }

        @Override
        public TransactionType getTransactionType() {
            return MofoTransactions.AccountIdAssignmentTransaction.ACCOUNT_ID_ASSIGNMENT;
        }

        public String getId() {
            return identifier;
        }

        public long getSignatory() {
            return signatory;
        }

        public byte[] getSignature() {
            return signature;
        }
    }

    public final static class VerificationAuthorityAssignmentAttachment extends AbstractAttachment {

        private final int period;

        VerificationAuthorityAssignmentAttachment(ByteBuffer buffer, byte transactionVersion) throws NxtException.NotValidException {
            super(buffer, transactionVersion);
            this.period = buffer.getInt();
        }

        VerificationAuthorityAssignmentAttachment(JSONObject attachmentData) {
            super(attachmentData);
            this.period = ((Long)attachmentData.get("period")).intValue();
        }

        public VerificationAuthorityAssignmentAttachment(int period) {
            this.period = period;
        }

        @Override
        int getMySize() {
            return 4;
        }

        @Override
        void putMyBytes(ByteBuffer buffer) {
            buffer.putInt(period);
        }

        @SuppressWarnings("unchecked")
        @Override
        void putMyJSON(JSONObject attachment) {
            attachment.put("period", period);
        }

        @Override
        public TransactionType getTransactionType() {
            return MofoTransactions.VerificationAuthorityAssignmentTransaction.VERIFICATION_AUTHORITY_ASSIGNMENT;
        }

        public int getPeriod() {
            return period;
        }
    }
}
