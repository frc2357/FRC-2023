import numpy as np
import logging

log = logging.getLogger()
log.addHandler(logging.NullHandler())
global gamepieces


class GamePieceTracker:
    # the following are singletons
    gamepiece_results = np.zeros((3, 9, 2), dtype=float)
    gamepiece_chars = np.chararray((3, 9), unicode=True)
    gamepiece_chars[:] = "-"  # set default value to dash

    def __init__(self):
        self.reset_results()
        # TODO: add NETWORK TABLES SUPPORT HERE

    def reset_results(self):
        self.gamepiece_results[:] = 0
        self.gamepiece_chars[:] = "-"

    def map_gamepiece_results(self, tag_id: int, idx: int, yel_pct: float, vio_pct: float):
        """takes gamepiece result information and maps it to self.gamepiece_results array"""
        # from left to right tag locations
        # tag [3]  [2]  [1]
        # tag [8]  [7]  [6]
        if tag_id in [4, 5]:
            log.error(f"tag_id out of range, was {tag_id}")
            return
        if idx < 0 or idx > 8:  # bounds check
            log.error(f"idx out of range, was {idx}")
            return
        tag_map = {3: 0, 2: 3, 1: 6, 8: 0, 7: 3, 6: 6}
        tag_offset = tag_map[tag_id]
        col = tag_offset + idx // 3
        row = idx % 3
        log.debug(f"map_gamepiece:tag_id {tag_id},row = {row},col = {col},tag_offset = {tag_offset}")
        # write new values to gamepiece_results
        # TODO: make these values sticky
        prev_yel, prev_vio = self.gamepiece_results[row, col, :]
        self.gamepiece_results[row, col, :] = [max(prev_yel, yel_pct), max(prev_vio, vio_pct)]

    def to_json(self):
        # convert gamepiece to characters
        self.gamepiece_chars[self.gamepiece_results[:, :, 0] == 1] = "A"
        self.gamepiece_chars[self.gamepiece_results[:, :, 1] == 1] = "O"
        ret = ["".join(x) for x in self.gamepiece_chars]
        ret.reverse()  # so that when printing, bottom row is the bottom!
        log.info("\n%s", "\n".join(ret))
        return json.dumps(ret)
        # return json.dumps({"gamepieces":["".join(x) for x in self.gamepiece_chars]})
        # return json.dumps({'cone':",".join([f"{x:0.2f}" for x in self.gamepiece_results[:,:,0].ravel()]),
        #                   'cube':self.gamepiece_results[:,:,1].tolist()},
        #                   separators=(',',':'))


gamepieces = GamePieceTracker()
