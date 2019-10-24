package game;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.pow;

public class TT {
    private final long[] zobristQ;
    private final long[] zobristR;
    private final long[] zobristS;
    private final int hashKeyBytes = 2;
    private final int hashKeyBits = hashKeyBytes*8;
    private TTElement[] tt;
    private long initHashKey;
    public int collisionCounter = 0;

    public class TTElement{
        public final long primaryHash;
        public final int value;
        public final byte depth;
        //flag: 0 exact value, 1: lower bound, 2:upper bound
        public final byte flag;

        //TODO: this can be expanded
        public TTElement(long primaryHash, int flag, int value, int depth){
            this.primaryHash = primaryHash;
            this.flag = (byte)flag;
            this.value = value;
            this.depth = (byte)depth;
        }
    }


    //fast way to convert long to bytes and back
    public static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= 8;
        }
        return result;
    }

    public static long bytesToLong(byte[] b) {
        long result = 0;
        for (int i = 0; i < b.length; i++) {
            result <<= 8;
            result |= (b[i] & 0xFF);
        }
        return result;
    }

    //Return the index if found, otherwise add it to the table and return -1
    public TTElement checkTT(long hash){
        //Most efficient way I could find to split long
        byte[] hashBytes = longToBytes(hash);

        //Long is 64 bits thus 8 bytes
        byte[] hashKeyByte = new byte[hashKeyBytes];
        byte[] primaryHashByte = new byte[8-hashKeyBytes];

        //Copy the hash into the two keys
        for(int i = 0; i < 8; i++){
            if(i < hashKeyBytes){
                hashKeyByte[i] = hashBytes[i];
            }else{
                primaryHashByte[i-hashKeyBytes] = hashBytes[i];
            }
        }

        //Convert them back to Long's
        long primaryHash = bytesToLong(primaryHashByte);
        int hashKey = (int)bytesToLong(hashKeyByte);

        //Check if it is present in the tt
        TTElement contents = tt[hashKey];
        if(contents == null){
            //Element is empty, add it
            //tt[hashKey] = new TTElement(primaryHash, value, depth);

            //Not found in tt, return -1
            return null;
        }else{
            //this hashKey is not emtpy, check for collision
            if(contents.primaryHash == primaryHash){
                //Collision occured
                return null;
            }
            else{
                //No collision, return the found element
                return contents;
            }
        }
    }

    public void storeTT(long hash, int flag, int value, int depth){
        //Most efficient way I could find to split long
        byte[] hashBytes = longToBytes(hash);

        //Long is 64 bits thus 8 bytes
        byte[] hashKeyByte = new byte[hashKeyBytes];
        byte[] primaryHashByte = new byte[8-hashKeyBytes];

        //Copy the hash into the two keys
        for(int i = 0; i < 8; i++){
            if(i < 8-hashKeyBytes){
                primaryHashByte[i] = hashBytes[i];
            }else{
                hashKeyByte[i-(8-hashKeyBytes)] = hashBytes[i];
            }
        }

        //Convert them back to Long's
        long primaryHash = bytesToLong(primaryHashByte);
        int hashKey = (int)bytesToLong(hashKeyByte);

        //Make the tt element
        TTElement ttElem = new TTElement(primaryHash, flag, value, depth);

        //Check if it is present in the tt
        TTElement contents = tt[hashKey];
        if(contents == null){
            //Element is empty, add it
            tt[hashKey] = ttElem;
        }else{
            //this hashKey is not emtpy, check for collision
            if(contents.primaryHash == primaryHash){
                //Collision occured
                //TODO: replacement strategy is here new, therefore do not return this value
                collisionCounter += 1;
                tt[hashKey] = ttElem;
            }
            else{
                //No collision, this one is already taken, you should not be here
            }
        }
    }

    public TT(int boardRadius){
        //Create the random values for every parameter
        zobristQ = new long[boardRadius*2+1];
        zobristR = new long[boardRadius*2+1];
        zobristS = new long[3];

        //Fill them with random long values
        for(int i = 0; i < zobristQ.length; i++){
            zobristQ[i] = ThreadLocalRandom.current().nextLong();
            zobristR[i] = ThreadLocalRandom.current().nextLong();
        }

        for(int i = 0; i < zobristS.length; i++){
            zobristS[i] = ThreadLocalRandom.current().nextLong();
        }

        //Create the transposition table with size 2^hashKeyBits
        this.tt = new TTElement[(int)pow(2,hashKeyBits)];
    }

    //Calculates the hash of a board
    public long getHash(Board board) {
        long hashKey = 0;
        int boardRadius = board.getBoardRadius();
        for(int q = 0; q < boardRadius*2+1; q++){
            for(int r = 0; r < boardRadius*2+1; r++){
                if(r + q >= boardRadius&& r + q <= 3*boardRadius) {
                    hashKey ^= zobristQ[q];
                    hashKey ^= zobristR[r];

                    if(board.getTileState(q, r) == 1){
                        hashKey ^= zobristS[1];
                    }else if(board.getTileState(q, r) == -1){
                        hashKey ^= zobristS[2];
                    }
                    else{
                        hashKey ^= zobristS[0];
                    }

                }
            }
        }
        return hashKey;
    }

    public long updateHash(long hashKey, Move move, int player) {
        hashKey ^= zobristQ[move.q];
        hashKey ^= zobristR[move.r];
        //Make sure the playernumber is converted to the array acces value
        int playerNum = 0;
        if(player == 0){
            playerNum = 1;
        }
        else if(player == -1){
            playerNum = 2;
        }
        hashKey ^= zobristS[playerNum];
        return hashKey;
    }


}
