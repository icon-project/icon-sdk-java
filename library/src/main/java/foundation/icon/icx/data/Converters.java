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
        public BigInteger convertTo(RpcField object) {
            RpcValue value = (RpcValue) object;
            return value.asInteger();
        }

        @Override
        public RpcField convertFrom(BigInteger object) {
            return RpcFieldCreator.create(object);
        }
    };

    public static final RpcConverter<Boolean> BOOLEAN
            = new RpcConverter<Boolean>() {

        @Override
        public Boolean convertTo(RpcField object) {
            RpcValue value = (RpcValue) object;
            return value.asBoolean();
        }

        @Override
        public RpcField convertFrom(Boolean object) {
            return RpcFieldCreator.create(object);
        }
    };

    public static final RpcConverter<String> STRING
            = new RpcConverter<String>() {

        @Override
        public String convertTo(RpcField object) {
            RpcValue value = (RpcValue) object;
            return value.asString();
        }

        @Override
        public RpcField convertFrom(String object) {
            return RpcFieldCreator.create(object);
        }
    };

    public static final RpcConverter<byte[]> BYTES
            = new RpcConverter<byte[]>() {

        @Override
        public byte[] convertTo(RpcField object) {
            RpcValue value = (RpcValue) object;
            return value.asBytes();
        }

        @Override
        public RpcField convertFrom(byte[] object) {
            return RpcFieldCreator.create(object);
        }
    };

    public static final RpcConverter<Block> BLOCK = new RpcConverter<Block>() {

        @Override
        public Block convertTo(RpcField object) {
            return new Block((RpcObject) object);
        }

        @Override
        public RpcField convertFrom(Block object) {
            return RpcFieldCreator.create(object);
        }
    };

    public static final RpcConverter<ConfirmedTransaction> CONFIRMED_TRANSACTION
            = new RpcConverter<ConfirmedTransaction>() {

        @Override
        public ConfirmedTransaction convertTo(RpcField object) {
            return new ConfirmedTransaction((RpcObject) object);
        }

        @Override
        public RpcField convertFrom(ConfirmedTransaction object) {
            return RpcFieldCreator.create(object);
        }
    };

    public static final RpcConverter<TransactionResult> TRANSACTION_RESULT
            = new RpcConverter<TransactionResult>() {

        @Override
        public TransactionResult convertTo(RpcField object) {
            return new TransactionResult((RpcObject) object);
        }

        @Override
        public RpcField convertFrom(TransactionResult object) {
            return RpcFieldCreator.create(object);
        }
    };

    public static final RpcConverter<List<ScoreApi>> SCORE_API_LIST
            = new RpcConverter<List<ScoreApi>>() {

        @Override
        public List<ScoreApi> convertTo(RpcField object) {
            RpcArray array = (RpcArray) object;
            List<ScoreApi> scoreApis = new ArrayList<>(array.size());
            for (int i = 0; i < array.size(); i++) {
                RpcObject rpcField = (RpcObject) array.get(i);
                scoreApis.add(new ScoreApi(rpcField));
            }
            return scoreApis;
        }

        @Override
        public RpcField convertFrom(List<ScoreApi> object) {
            return RpcFieldCreator.create(object);
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
