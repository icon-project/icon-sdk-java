/*
 * Copyright 2018 theloop Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package foundation.icon.icx.data;

import foundation.icon.icx.transport.jsonrpc.*;
import foundation.icon.icx.transport.jsonrpc.RpcConverter.RpcConverterFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public final class Converters {
    private Converters() {}

    public static final RpcConverter<BigInteger> BIG_INTEGER
            = new RpcConverter<BigInteger>() {

        @Override
        public BigInteger convertTo(RpcItem object) {
            return object.asInteger();
        }

        @Override
        public RpcItem convertFrom(BigInteger object) {
            return RpcItemCreator.create(object);
        }
    };

    public static final RpcConverter<Boolean> BOOLEAN
            = new RpcConverter<Boolean>() {

        @Override
        public Boolean convertTo(RpcItem object) {
            return object.asBoolean();
        }

        @Override
        public RpcItem convertFrom(Boolean object) {
            return RpcItemCreator.create(object);
        }
    };

    public static final RpcConverter<String> STRING
            = new RpcConverter<String>() {

        @Override
        public String convertTo(RpcItem object) {
            return object.asString();
        }

        @Override
        public RpcItem convertFrom(String object) {
            return RpcItemCreator.create(object);
        }
    };

    public static final RpcConverter<byte[]> BYTES
            = new RpcConverter<byte[]>() {

        @Override
        public byte[] convertTo(RpcItem object) {
            return object.asBytes();
        }

        @Override
        public RpcItem convertFrom(byte[] object) {
            return RpcItemCreator.create(object);
        }
    };

    public static final RpcConverter<Block> BLOCK = new RpcConverter<Block>() {

        @Override
        public Block convertTo(RpcItem object) {
            return new Block(object.asObject());
        }

        @Override
        public RpcItem convertFrom(Block object) {
            return RpcItemCreator.create(object);
        }
    };

    public static final RpcConverter<ConfirmedTransaction> CONFIRMED_TRANSACTION
            = new RpcConverter<ConfirmedTransaction>() {

        @Override
        public ConfirmedTransaction convertTo(RpcItem object) {
            return new ConfirmedTransaction(object.asObject());
        }

        @Override
        public RpcItem convertFrom(ConfirmedTransaction object) {
            return RpcItemCreator.create(object);
        }
    };

    public static final RpcConverter<TransactionResult> TRANSACTION_RESULT
            = new RpcConverter<TransactionResult>() {

        @Override
        public TransactionResult convertTo(RpcItem object) {
            return new TransactionResult(object.asObject());
        }

        @Override
        public RpcItem convertFrom(TransactionResult object) {
            return RpcItemCreator.create(object);
        }
    };

    public static final RpcConverter<List<ScoreApi>> SCORE_API_LIST
            = new RpcConverter<List<ScoreApi>>() {

        @Override
        public List<ScoreApi> convertTo(RpcItem rpcItem) {
            RpcArray array = rpcItem.asArray();
            List<ScoreApi> scoreApis = new ArrayList<>(array.size());
            for (int i = 0; i < array.size(); i++) {
                scoreApis.add(new ScoreApi(array.get(i).asObject()));
            }
            return scoreApis;
        }

        @Override
        public RpcItem convertFrom(List<ScoreApi> object) {
            return RpcItemCreator.create(object);
        }
    };

    public static <TT> RpcConverterFactory newFactory(
            final Class<TT> typeFor, final RpcConverter<TT> converter) {
        return new RpcConverterFactory() {
            @Override
            public <T> RpcConverter<T> create(Class<T> type) {
                return type.isAssignableFrom(typeFor) ? (RpcConverter<T>) converter : null;
            }
        };
    }
}
