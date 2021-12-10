import { axiosBackend } from "../../../config/axios";

export default function handler(req, res) {
    if(req.method === 'PUT') {
        axiosBackend.put(`/items/${req.query.uuid}`, req.body, req.headers).then(response => {
            res.status(200).json(response.data);
        })
    }
    else if(req.method === 'DELETE') {
        axiosBackend.delete(`/items/${req.query.uuid}`, req.body, req.headers).then(response => {
            res.status(200).json(response.data);
        })
    }
}

